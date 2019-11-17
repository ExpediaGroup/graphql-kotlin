/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.federation.tracing

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.google.protobuf.Timestamp
import graphql.ExecutionResult
import graphql.ExecutionResultImpl
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.execution.DataFetcherResult
import graphql.execution.ExecutionPath
import graphql.execution.ExecutionStepInfo
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.SimpleInstrumentationContext.whenCompleted
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.language.Document
import graphql.language.SourceLocation
import graphql.parser.InvalidSyntaxException
import graphql.schema.GraphQLTypeUtil.simplePrint

import mdg.engine.proto.Reports
import java.time.Instant
import java.util.ArrayList
import java.util.Base64
import java.util.concurrent.CompletableFuture
import kotlin.collections.LinkedHashMap

class FederatedTracingInstrumentation : SimpleInstrumentation() {
    companion object {
        const val HEADER_NAME: String = "apollo-federation-include-trace"
        const val HEADER_VALUE: String = "ftv1"
        const val EXTENSION_KEY: String = "ftv1"
    }

    override fun createState(parameters: InstrumentationCreateStateParameters): InstrumentationState? {
        val context = parameters.executionInput.context
        if (context is HttpRequestHeaders) {
            val headerValue = context.getHTTPRequestHeader(HEADER_NAME)
            if (headerValue == null || headerValue != HEADER_VALUE) {
                return null
            }
        }
        return FederatedTracingState()
    }

    override fun instrumentExecutionResult(executionResult: ExecutionResult?, parameters: InstrumentationExecutionParameters): CompletableFuture<ExecutionResult> {
        val state = parameters.getInstrumentationState<FederatedTracingState>() ?: return super.instrumentExecutionResult(executionResult, parameters)

        val trace = state.toProto()

        return CompletableFuture.completedFuture(ExecutionResultImpl.newExecutionResult()
            .data(executionResult?.getData())
            .errors(executionResult?.errors)
            .extensions(executionResult?.extensions)
            .addExtension(EXTENSION_KEY, Base64.getEncoder().encodeToString(trace.toByteArray()))
            .build())
    }

    override fun beginFieldFetch(parameters: InstrumentationFieldFetchParameters): InstrumentationContext<Any> {
        val state = parameters.getInstrumentationState<FederatedTracingState>() ?: return super.beginFieldFetch(parameters)

        val fieldLocation = parameters.environment.field.sourceLocation

        val startNanos = System.nanoTime()
        return whenCompleted { result, throwable ->
            val endNanos = System.nanoTime()

            val executionStepInfo = parameters.environment.executionStepInfo
            state.run {
                addNode(
                    executionStepInfo,
                    startNanos - state.getStartRequestNanos(),
                    endNanos - state.getStartRequestNanos(),
                    convertErrors(throwable, result),
                    fieldLocation
                )
            }
        }
    }

    override fun beginParse(parameters: InstrumentationExecutionParameters): InstrumentationContext<Document> {
        val state = parameters.getInstrumentationState<FederatedTracingState>() ?: return super.beginParse(parameters)

        return whenCompleted { _, throwable ->
            for (error in convertErrors(throwable, null)) {
                state.run { addRootError(error) }
            }
        }
    }

    // Field resolvers can throw exceptions or add errors to the DataFetchingResult. This method normalizes them to a
    // single list of GraphQLErrors.
    private fun convertErrors(throwable: Throwable?, result: Any?): List<GraphQLError> {
        val graphQLErrors = ArrayList<GraphQLError>()

        if (throwable != null) {
            graphQLErrors.add(getThrowableError(throwable))
        }

        if (result is DataFetcherResult<*>) {
            val theResult = result as DataFetcherResult<*>?
            if (theResult!!.run { hasErrors() }) {
                graphQLErrors.run { addAll(theResult.errors) }
            }
        }

        return graphQLErrors
    }

    private fun getThrowableError(throwable: Throwable): GraphQLError {
        if (throwable is GraphQLError) {
            return throwable
        } else {
            var message: String? = throwable.message

            if (message == null) {
                message = "(null)"
            }

            val errorBuilder = GraphqlErrorBuilder.newError().message(message)

            if (throwable is InvalidSyntaxException) {
                errorBuilder.location(throwable.location)
            }

            return errorBuilder.build()
        }
    }

    // TODO give necessary credit to federation-jvm
    private class FederatedTracingState : InstrumentationState {
        private var startRequestTime: Instant = Instant.now()
        private var startRequestNanos: Long = System.nanoTime()
        private var nodesByPath: LinkedHashMap<ExecutionPath, Reports.Trace.Node.Builder> = java.util.LinkedHashMap()

        init {
            nodesByPath[ExecutionPath.rootPath()] = Reports.Trace.Node.newBuilder()
        }

        internal fun toProto(): Reports.Trace = Reports.Trace.newBuilder()
            .setStartTime(getStartTimestamp())
            .setEndTime(getNowTimestamp())
            .setDurationNs(getDuration())
            .setRoot(nodesByPath[ExecutionPath.rootPath()])
            .build()

        internal fun addNode(
            stepInfo: ExecutionStepInfo,
            startFieldNanos: Long,
            endFieldNanos: Long,
            errors: List<GraphQLError>,
            fieldLocation: SourceLocation?
        ) {
            val path = stepInfo.path
            val parent = getParentNode(path)

            val node = parent.addChildBuilder()
                .setStartTime(startFieldNanos)
                .setEndTime(endFieldNanos)
                .setParentType(simplePrint(stepInfo.parent.unwrappedNonNullType))
                .setType(stepInfo.simplePrint())
                .setResponseName(stepInfo.resultKey)

            val originalFieldName = stepInfo.field.name

            // set originalFieldName only when a field alias was used
            if (originalFieldName != stepInfo.resultKey) {
                node.originalFieldName = originalFieldName
            }

            errors.forEach { error ->
                val builder = node.addErrorBuilder().setMessage(error.message)
                if (error.locations.isEmpty() && fieldLocation != null) {
                    builder.addLocationBuilder()
                        .setColumn(fieldLocation.column).line = fieldLocation.line
                } else {
                    error.locations.forEach { location ->
                        builder.addLocationBuilder()
                            .setColumn(location.column).line = location.line
                    }
                }
            }

            nodesByPath[path] = node
        }

        internal fun getParentNode(path: ExecutionPath): Reports.Trace.Node.Builder {
            val pathParts = path.toList()
            return nodesByPath.computeIfAbsent(ExecutionPath.fromList(pathParts.subList(0, pathParts.size - 1))) { parentPath ->
                if (parentPath == ExecutionPath.rootPath()) {
                    // The root path is inserted at construction time, so this shouldn't happen.
                    throw GraphQLKotlinException("root path missing from nodesByPath")
                }

                // Recursively get the grandparent node and start building the parent node.
                val missingParent = getParentNode(parentPath).addChildBuilder()

                // If the parent was a field name, then its fetcher would have been called before
                // the fetcher for 'path' and it would be in nodesByPath. So the parent must be
                // a list index.  Note that we subtract 2 here because we want the last part of
                // parentPath, not path.
                val parentLastPathPart = pathParts[pathParts.size - 2]
                if (parentLastPathPart !is Int) {
                    throw GraphQLKotlinException("Unexpected missing non-index $parentLastPathPart")
                }
                missingParent.index = parentLastPathPart
                missingParent
            }
        }

        internal fun addRootError(error: GraphQLError) {
            val builder = nodesByPath[ExecutionPath.rootPath()]?.addErrorBuilder()?.setMessage(error.message)

            error.locations.forEach { location ->
                builder?.addLocationBuilder()
                    ?.setColumn(location.column)?.line = location.line
            }
        }

        internal fun getStartRequestNanos(): Long = startRequestNanos

        private fun getStartTimestamp(): Timestamp = instantToTimestamp(startRequestTime)

        private fun getNowTimestamp(): Timestamp = instantToTimestamp(Instant.now())

        private fun instantToTimestamp(startRequestTime: Instant): Timestamp = Timestamp.newBuilder()
            .setSeconds(startRequestTime.epochSecond)
            .setNanos(startRequestTime.nano).build()

        private fun getDuration(): Long = System.nanoTime() - startRequestNanos
    }
}
