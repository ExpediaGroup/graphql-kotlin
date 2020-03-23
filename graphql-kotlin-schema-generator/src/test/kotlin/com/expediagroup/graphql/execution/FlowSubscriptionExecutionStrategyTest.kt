/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.execution

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.hooks.FlowSubscriptionSchemaGeneratorHooks
import com.expediagroup.graphql.toSchema
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.GraphQLSchema
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asPublisher
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@InternalCoroutinesApi
class FlowSubscriptionExecutionStrategyTest {

    private val testSchema: GraphQLSchema = toSchema(
        config = SchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.spring.execution"),
            hooks = FlowSubscriptionSchemaGeneratorHooks()
        ),
        queries = listOf(TopLevelObject(BasicQuery())),
        mutations = listOf(TopLevelObject(BasicQuery())),
        subscriptions = listOf(TopLevelObject(FlowSubscription()))
    )
    private val testGraphQL: GraphQL = GraphQL.newGraphQL(testSchema).subscriptionExecutionStrategy(FlowSubscriptionExecutionStrategy()).build()

    @Test
    fun `verify subscription to flow`() = runBlocking {
        val request = ExecutionInput.newExecutionInput().query("subscription { ticker }").build()
        val response = testGraphQL.execute(request)
        val flow = response.getData<Flow<ExecutionResult>>()
        val list = mutableListOf<Int>()
        flow.collect {
            list.add(it.getData<Map<String, Int>>().getValue("ticker"))
        }
        assertEquals(5, list.size)
        for (i in list.indices) {
            assertEquals(i + 1, list[i])
        }
    }

    @Test
    fun `verify subscription to publisher`() = runBlocking {
        val request = ExecutionInput.newExecutionInput().query("subscription { publisherTicker }").build()
        val response = testGraphQL.execute(request)
        val flow = response.getData<Flow<ExecutionResult>>()
        val list = mutableListOf<Int>()
        flow.collect {
            list.add(it.getData<Map<String, Int>>().getValue("publisherTicker"))
        }
        assertEquals(5, list.size)
        for (i in list.indices) {
            assertEquals(i + 1, list[i])
        }
    }

    @Test
    fun `verify subscription to flow with context`() = runBlocking {
        val request = ExecutionInput.newExecutionInput()
            .query("subscription { contextualTicker }")
            .context(SubscriptionContext("junitHandler"))
            .build()
        val response = testGraphQL.execute(request)
        val flow = response.getData<Flow<ExecutionResult>>()
        val list = mutableListOf<Int>()
        flow.collect {
            val contextValue = it.getData<Map<String, String>>().getValue("contextualTicker")
            assertTrue(contextValue.startsWith("junitHandler:"))
            list.add(contextValue.substringAfter("junitHandler:").toInt())
        }
        assertEquals(5, list.size)
        for (i in list.indices) {
            assertEquals(i + 1, list[i])
        }
    }

    @Test
    fun `verify subscription to failing flow`() = runBlocking {
        val request = ExecutionInput.newExecutionInput().query("subscription { alwaysThrows }").build()
        val response = testGraphQL.execute(request)
        val flow = response.getData<Flow<ExecutionResult>>()
        val errors = mutableListOf<GraphQLError>()
        val results = mutableListOf<Int>()
        flow.onEach {
            val dataMap = it.getData<Map<String, Int>>()
            if (dataMap != null) {
                results.add(dataMap.getValue("alwaysThrows"))
            }
            errors.addAll(it.errors)
        }.catch {
            errors.add(GraphqlErrorBuilder.newError().message(it.message).build())
        }.collect()
        assertEquals(2, results.size)
        for (i in results.indices) {
            assertEquals(i + 1, results[i])
        }
        assertEquals(1, errors.size)
        assertEquals("JUNIT subscription failure", errors[0].message)
    }

    @Test
    fun `verify subscription to exploding flow`() = runBlocking {
        val request = ExecutionInput.newExecutionInput().query("subscription { throwsFast }").build()
        val response = testGraphQL.execute(request)
        val flow = response.getData<Flow<ExecutionResult>>()
        val errors = response.errors
        assertNull(flow)
        assertEquals(1, errors.size)
        assertEquals("JUNIT flow failure", errors[0].message.substringAfter(" : "))
    }

    @Test
    fun `verify subscription alias`() = runBlocking {
        val request = ExecutionInput.newExecutionInput().query("subscription { t: ticker }").build()
        val response = testGraphQL.execute(request)
        val flow = response.getData<Flow<ExecutionResult>>()
        val list = mutableListOf<Int>()
        flow.collect {
            list.add(it.getData<Map<String, Int>>().getValue("t"))
        }
        assertEquals(5, list.size)
        for (i in list.indices) {
            assertEquals(i + 1, list[i])
        }
    }

    // GraphQL spec requires at least single query to be present as Query type is needed to run introspection queries
    // see: https://github.com/graphql/graphql-spec/issues/490 and https://github.com/graphql/graphql-spec/issues/568
    class BasicQuery {
        @Suppress("Detekt.FunctionOnlyReturningConstant")
        fun query(): String = "hello"
    }

    class FlowSubscription {
        fun ticker(): Flow<Int> {
            return flow {
                for (i in 1..5) {
                    delay(100)
                    emit(i)
                }
            }
        }

        fun publisherTicker(): Publisher<Int> {
            return flow {
                for (i in 1..5) {
                    delay(100)
                    emit(i)
                }
            }.asPublisher()
        }

        fun throwsFast(): Flow<Int> {
            throw GraphQLKotlinException("JUNIT flow failure")
        }

        fun alwaysThrows(): Flow<Int> {
            return flow {
                for (i in 1..5) {
                    if (i > 2) {
                        throw GraphQLKotlinException("JUNIT subscription failure")
                    }
                    delay(100)
                    emit(i)
                }
            }
        }

        fun contextualTicker(context: SubscriptionContext): Flow<String> {
            return flow {
                for (i in 1..5) {
                    delay(100)
                    emit("${context.value}:$i")
                }
            }
        }
    }

    data class SubscriptionContext(val value: String) : GraphQLContext
}
