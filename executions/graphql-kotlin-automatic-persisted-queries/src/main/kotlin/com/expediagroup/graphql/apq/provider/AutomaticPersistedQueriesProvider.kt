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

import com.expediagroup.graphql.apq.cache.AutomaticPersistedQueriesCache
import com.expediagroup.graphql.apq.extensions.getAutomaticPersistedQueriesExtension
import com.expediagroup.graphql.apq.extensions.getQueryId
import com.expediagroup.graphql.apq.extensions.isAutomaticPersistedQueriesExtensionInvalid
import graphql.ExecutionInput
import graphql.GraphqlErrorBuilder
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.execution.preparsed.persisted.PersistedQueryError
import graphql.execution.preparsed.persisted.PersistedQueryIdInvalid
import graphql.execution.preparsed.persisted.PersistedQueryNotFound
import java.util.concurrent.CompletableFuture
import java.util.function.Function

class AutomaticPersistedQueriesProvider(
    private val cache: AutomaticPersistedQueriesCache
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
    ): CompletableFuture<PreparsedDocumentEntry> =
        try {
            executionInput.getAutomaticPersistedQueriesExtension()?.let { apqExtension ->
                cache.getPersistedQueryDocumentAsync(apqExtension.sha256Hash, executionInput) { query ->
                    when {
                        query.isBlank() -> {
                            throw PersistedQueryNotFound(apqExtension.sha256Hash)
                        }
                        executionInput.isAutomaticPersistedQueriesExtensionInvalid(apqExtension) -> {
                            throw PersistedQueryIdInvalid(apqExtension.sha256Hash)
                        }
                        else -> {
                            parseAndValidateFunction.apply(
                                executionInput.transform { builder -> builder.query(query) }
                            )
                        }
                    }
                }
            } ?: run {
                // no apqExtension, not a persisted query,
                // but we still want to cache the parsed and validated document
                cache.getOrElse(executionInput.getQueryId()) {
                    parseAndValidateFunction.apply(executionInput)
                }
            }
        } catch (persistedQueryError: PersistedQueryError) {
            CompletableFuture.completedFuture(
                PreparsedDocumentEntry(
                    GraphqlErrorBuilder.newError()
                        .errorType(persistedQueryError)
                        .message(persistedQueryError.message)
                        .extensions(
                            when (persistedQueryError) {
                                // persistedQueryError.getExtensions()
                                // Cannot access 'getExtensions': it is package-private in 'PersistedQueryError'
                                is PersistedQueryNotFound -> persistedQueryError.extensions
                                is PersistedQueryIdInvalid -> persistedQueryError.extensions
                                else -> emptyMap()
                            }
                        ).build()
                )
            )
        }
}
