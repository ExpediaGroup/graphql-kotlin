/*
 * Copyright 2022 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.execution

import graphql.ExecutionResult
import graphql.ExecutionResultImpl
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.ExecutionContext
import graphql.execution.ExecutionStepInfo
import graphql.execution.ExecutionStrategy
import graphql.execution.ExecutionStrategyParameters
import graphql.execution.SimpleDataFetcherExceptionHandler
import graphql.execution.SubscriptionExecutionStrategy
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentationContext
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldParameters
import graphql.schema.GraphQLObjectType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.Publisher
import java.util.Collections
import java.util.concurrent.CompletableFuture

/**
 * [SubscriptionExecutionStrategy] replacement that and allows schema subscription functions
 * to return either a [Flow] or a [Publisher].
 *
 * Note this implementation is mostly a java->kotlin copy of [SubscriptionExecutionStrategy],
 * with updated [createSourceEventStream] that supports [Flow] and [Publisher]. Any returned
 * [Flow]s will be automatically converted to corresponding [Publisher].
 */
class FlowSubscriptionExecutionStrategy(dfe: DataFetcherExceptionHandler) : ExecutionStrategy(dfe) {
    constructor() : this(SimpleDataFetcherExceptionHandler())

    override fun execute(
        executionContext: ExecutionContext,
        parameters: ExecutionStrategyParameters
    ): CompletableFuture<ExecutionResult> {

        val instrumentation = executionContext.instrumentation
        val instrumentationParameters = InstrumentationExecutionStrategyParameters(executionContext, parameters)
        val executionStrategyCtx = ExecutionStrategyInstrumentationContext.nonNullCtx(
            instrumentation.beginExecutionStrategy(
                instrumentationParameters,
                executionContext.instrumentationState
            )
        )

        val sourceEventStream = createSourceEventStream(executionContext, parameters)

        //
        // when the upstream source event stream completes, subscribe to it and wire in our adapter
        val overallResult: CompletableFuture<ExecutionResult> = sourceEventStream.thenApply { flow ->
            if (flow == null) {
                ExecutionResultImpl(null, executionContext.errors)
            } else {
                val returnFlow = flow.map { eventPayload: Any? ->
                    executeSubscriptionEvent(
                        executionContext,
                        parameters,
                        eventPayload
                    ).await()
                }
                ExecutionResultImpl(returnFlow, executionContext.errors)
            }
        }

        // dispatched the subscription query
        executionStrategyCtx.onDispatched(overallResult)
        overallResult.whenComplete(executionStrategyCtx::onCompleted)

        return overallResult
    }

    /*
        https://github.com/facebook/graphql/blob/master/spec/Section%206%20--%20Execution.md

        CreateSourceEventStream(subscription, schema, variableValues, initialValue):

            Let {subscriptionType} be the root Subscription type in {schema}.
            Assert: {subscriptionType} is an Object type.
            Let {selectionSet} be the top level Selection Set in {subscription}.
            Let {rootField} be the first top level field in {selectionSet}.
            Let {argumentValues} be the result of {CoerceArgumentValues(subscriptionType, rootField, variableValues)}.
            Let {fieldStream} be the result of running {ResolveFieldEventStream(subscriptionType, initialValue, rootField, argumentValues)}.
            Return {fieldStream}.
     */
    private fun createSourceEventStream(
        executionContext: ExecutionContext,
        parameters: ExecutionStrategyParameters
    ): CompletableFuture<Flow<*>?> {
        val newParameters = firstFieldOfSubscriptionSelection(parameters)

        val fieldFetched = fetchField(executionContext, newParameters)
        return fieldFetched.thenApply { fetchedValue ->
            val flow = when (val publisherOrFlow: Any? = fetchedValue.fetchedValue) {
                is Publisher<*> -> publisherOrFlow.asFlow()
                // below explicit cast is required due to the type erasure and Kotlin declaration-site variance vs Java use-site variance
                is Flow<*> -> publisherOrFlow
                else -> null
            }
            flow
        }
    }

    /*
        ExecuteSubscriptionEvent(subscription, schema, variableValues, initialValue):

        Let {subscriptionType} be the root Subscription type in {schema}.
        Assert: {subscriptionType} is an Object type.
        Let {selectionSet} be the top level Selection Set in {subscription}.
        Let {data} be the result of running {ExecuteSelectionSet(selectionSet, subscriptionType, initialValue, variableValues)} normally (allowing parallelization).
        Let {errors} be any field errors produced while executing the selection set.
        Return an unordered map containing {data} and {errors}.

        Note: The {ExecuteSubscriptionEvent()} algorithm is intentionally similar to {ExecuteQuery()} since this is how each event result is produced.
     */
    private fun executeSubscriptionEvent(
        executionContext: ExecutionContext,
        parameters: ExecutionStrategyParameters,
        eventPayload: Any?
    ): CompletableFuture<ExecutionResult> {
        val instrumentation = executionContext.instrumentation

        val newExecutionContext = executionContext.transform { builder ->
            builder
                .root(eventPayload)
                .resetErrors()
        }
        val newParameters = firstFieldOfSubscriptionSelection(parameters)
        val subscribedFieldStepInfo = createSubscribedFieldStepInfo(executionContext, newParameters)

        val i13nFieldParameters = InstrumentationFieldParameters(executionContext) { subscribedFieldStepInfo }
        val subscribedFieldCtx = SimpleInstrumentationContext.nonNullCtx(
            instrumentation.beginSubscribedFieldEvent(
                i13nFieldParameters, executionContext.instrumentationState
            )
        )

        val fetchedValue = unboxPossibleDataFetcherResult(newExecutionContext, parameters, eventPayload)

        val fieldValueInfo = completeField(newExecutionContext, newParameters, fetchedValue)
        val overallResult = fieldValueInfo
            .fieldValue
            .thenApply { executionResult -> wrapWithRootFieldName(newParameters, executionResult) }

        // dispatch instrumentation so they can know about each subscription event
        subscribedFieldCtx.onDispatched(overallResult)
        overallResult.whenComplete(subscribedFieldCtx::onCompleted)

        // allow them to instrument each ER should they want to
        val i13nExecutionParameters = InstrumentationExecutionParameters(
            executionContext.executionInput, executionContext.graphQLSchema, executionContext.instrumentationState
        )

        return overallResult.thenCompose { executionResult ->
            instrumentation.instrumentExecutionResult(executionResult, i13nExecutionParameters, executionContext.instrumentationState)
        }
    }

    private fun wrapWithRootFieldName(
        parameters: ExecutionStrategyParameters,
        executionResult: ExecutionResult
    ): ExecutionResult {
        val rootFieldName = getRootFieldName(parameters)
        return ExecutionResultImpl(
            Collections.singletonMap<String, Any>(rootFieldName, executionResult.getData<Any>()),
            executionResult.errors
        )
    }

    private fun getRootFieldName(parameters: ExecutionStrategyParameters): String {
        val rootField = parameters.field.singleField
        return if (rootField.alias != null) rootField.alias else rootField.name
    }

    private fun firstFieldOfSubscriptionSelection(
        parameters: ExecutionStrategyParameters
    ): ExecutionStrategyParameters {
        val fields = parameters.fields
        val firstField = fields.getSubField(fields.keys[0])

        val fieldPath = parameters.path.segment(mkNameForPath(firstField.singleField))
        return parameters.transform { builder -> builder.field(firstField).path(fieldPath) }
    }

    private fun createSubscribedFieldStepInfo(
        executionContext: ExecutionContext,
        parameters: ExecutionStrategyParameters
    ): ExecutionStepInfo {
        val field = parameters.field.singleField
        val parentType = parameters.executionStepInfo.unwrappedNonNullType as GraphQLObjectType
        val fieldDef = getFieldDef(executionContext.graphQLSchema, parentType, field)
        return createExecutionStepInfo(executionContext, parameters, fieldDef, parentType)
    }
}
