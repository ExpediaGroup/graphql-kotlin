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

package com.expediagroup.graphql.apq.provider

import com.expediagroup.graphql.apq.cache.AutomaticPersistedQueryCache
import graphql.ExecutionInput
import graphql.GraphqlErrorBuilder
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.execution.preparsed.persisted.PersistedQueryError
import graphql.execution.preparsed.persisted.PersistedQueryIdInvalid
import graphql.execution.preparsed.persisted.PersistedQueryNotFound
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.CompletableFuture
import java.util.function.Function

class AutomaticPersistedQueryProvider(
    private val cache: AutomaticPersistedQueryCache
) : PreparsedDocumentProvider {

    @Deprecated(
        "deprecated in favor of async retrieval of Document",
        ReplaceWith("this.getDocumentAsync(executionInput, parseAndValidateFunction).get()")
    )
    override fun getDocument(
        executionInput: ExecutionInput,
        parseAndValidateFunction: Function<ExecutionInput, PreparsedDocumentEntry>
    ): PreparsedDocumentEntry =
        this.getDocumentAsync(
            executionInput,
            parseAndValidateFunction
        ).get()

    override fun getDocumentAsync(
        executionInput: ExecutionInput,
        parseAndValidateFunction: Function<ExecutionInput, PreparsedDocumentEntry>
    ): CompletableFuture<PreparsedDocumentEntry> = try {
        getPersistedQueryId(executionInput)?.let { persistedQueryId ->
            cache.getPersistedQueryDocumentAsync(persistedQueryId, executionInput) { query ->
                when {
                    query.isNullOrBlank() -> {
                        throw PersistedQueryNotFound(persistedQueryId)
                    }
                    !isPersistedQueryIdValid(persistedQueryId, query) -> {
                        throw PersistedQueryIdInvalid(persistedQueryId)
                    }
                    else -> {
                        val newExecutionInput = executionInput.transform { builder -> builder.query(query) }
                        parseAndValidateFunction.apply(newExecutionInput)
                    }
                }
            }
        } ?: run {
            // no persistedQueryId, not a persisted query, ready to go
            CompletableFuture.completedFuture(parseAndValidateFunction.apply(executionInput))
        }
    } catch (persistedQueryError: PersistedQueryError) {
        CompletableFuture.completedFuture(
            PreparsedDocumentEntry(
                GraphqlErrorBuilder.newError()
                    .errorType(persistedQueryError).message(persistedQueryError.message)
                    .extensions(
                        when (persistedQueryError) {
                            // persistedQueryError.getExtensions().
                            // Cannot access 'getExtensions': it is package-private in 'PersistedQueryError'
                            is PersistedQueryNotFound -> persistedQueryError.extensions
                            is PersistedQueryIdInvalid -> persistedQueryError.extensions
                            else -> emptyMap()
                        }
                    ).build()
            )
        )
    }

    private fun getPersistedQueryId(
        executionInput: ExecutionInput
    ): String? =
        (executionInput.extensions["persistedQuery"] as? Map<*, *>)?.get("sha256Hash") as? String

    private fun isPersistedQueryIdValid(
        persistedQueryId: String,
        query: String
    ): Boolean = try {
        val bigInteger = BigInteger(
            1,
            MessageDigest.getInstance("SHA-256").digest(query.toByteArray(StandardCharsets.UTF_8))
        )
        val calculatedPersistedQueryId = String.format("%064x", bigInteger)
        calculatedPersistedQueryId.equals(persistedQueryId, ignoreCase = true)
    } catch (e: NoSuchAlgorithmException) {
        false
    }
}
