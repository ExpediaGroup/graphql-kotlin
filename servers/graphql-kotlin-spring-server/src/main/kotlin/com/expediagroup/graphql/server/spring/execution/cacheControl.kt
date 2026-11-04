/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.server.spring.execution

import com.apollographql.federation.graphqljava.caching.CacheControlInstrumentation
import com.expediagroup.graphql.server.types.GraphQLBatchResponse
import com.expediagroup.graphql.server.types.GraphQLResponse
import com.expediagroup.graphql.server.types.GraphQLServerResponse
import graphql.ExecutionResult
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import org.springframework.web.reactive.function.server.ServerRequest
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * `Cache-Control` value used for responses that must not be cached, matching Apollo Server behavior.
 */
internal const val CACHE_CONTROL_NO_STORE = "no-store"

/**
 * [ServerRequest] attribute holding the [CacheControlPolicies] of the operations executed for that request.
 */
internal const val CACHE_CONTROL_POLICIES_ATTRIBUTE = "com.expediagroup.graphql.server.spring.execution.CacheControlPolicies"

private val CACHE_CONTROL_POLICY_REGEX = Regex("""max-age=(\d+), (public|private)""")

/**
 * Collects the cache policy calculated for every operation executed as part of a single HTTP request. Operations are
 * executed with their own copy of the request [graphql.GraphQLContext], so this holder is shared by reference.
 * Operations without a cacheable policy are recorded as uncacheable.
 */
internal class CacheControlPolicies {
    private val policies = ConcurrentLinkedQueue<CacheControlPolicy>()

    fun add(header: String?) {
        val policy = header
            ?.let(CACHE_CONTROL_POLICY_REGEX::matchEntire)
            ?.let { match -> CacheControlPolicy(match.groupValues[1].toInt(), match.groupValues[2] == "private") }
        policies.add(policy ?: CacheControlPolicy.UNCACHEABLE)
    }

    /**
     * Combines the policies of [operations] operations into a single `Cache-Control` value: the lowest max age wins and
     * a single private operation makes the response private. Returns `null` if any operation is not cacheable.
     */
    fun headerValue(operations: Int): String? {
        val collected = policies.toList()
        if (operations == 0 || collected.size != operations || collected.any { it === CacheControlPolicy.UNCACHEABLE }) {
            return null
        }
        val maxAge = collected.minOf { it.maxAge }
        val scope = if (collected.any { it.private }) "private" else "public"
        return "max-age=$maxAge, $scope"
    }

    private data class CacheControlPolicy(val maxAge: Int, val private: Boolean) {
        companion object {
            val UNCACHEABLE = CacheControlPolicy(-1, false)
        }
    }
}

/**
 * Federation-jvm [CacheControlInstrumentation] that also records the policy calculated for each operation into the
 * [CacheControlPolicies] of the HTTP request, so that the route can return it in the `Cache-Control` response header.
 */
internal class SpringCacheControlInstrumentation(defaultMaxAge: Int) : CacheControlInstrumentation(defaultMaxAge) {
    override fun beginExecution(
        parameters: InstrumentationExecutionParameters,
        state: InstrumentationState?
    ): InstrumentationContext<ExecutionResult>? {
        val delegate = super.beginExecution(parameters, state)
        return object : InstrumentationContext<ExecutionResult> {
            override fun onDispatched() {
                delegate?.onDispatched()
            }

            override fun onCompleted(result: ExecutionResult?, throwable: Throwable?) {
                delegate?.onCompleted(result, throwable)
                val context = parameters.graphQLContext
                context.get<CacheControlPolicies>(CacheControlPolicies::class)
                    ?.add(cacheControlHeaderFromGraphQLContext(context))
            }
        }
    }
}

/**
 * Calculates the `Cache-Control` response header, mirroring the Apollo Server cache control plugin:
 * - operations without errors and with a cacheable policy get `max-age=<seconds>, <public|private>`
 *   (batched operations are combined into the most restrictive policy)
 * - anything else gets `no-store`
 */
internal fun cacheControlHeaderValue(request: ServerRequest, response: GraphQLServerResponse): String {
    val responses = when (response) {
        is GraphQLResponse<*> -> listOf(response)
        is GraphQLBatchResponse -> response.responses
    }
    val policies = request.attribute(CACHE_CONTROL_POLICIES_ATTRIBUTE).orElse(null) as? CacheControlPolicies
    return policies
        ?.takeIf { responses.all { it.errors.isNullOrEmpty() } }
        ?.headerValue(responses.size)
        ?: CACHE_CONTROL_NO_STORE
}
