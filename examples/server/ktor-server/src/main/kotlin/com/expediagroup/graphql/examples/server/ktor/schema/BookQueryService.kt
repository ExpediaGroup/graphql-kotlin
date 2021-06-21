/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.examples.server.ktor.schema

import com.expediagroup.graphql.examples.server.ktor.schema.models.Book
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query

/**
 * Provide Search options for book data
 */
class BookQueryService : Query {
    @GraphQLDescription("Return list of books based on BookSearchParameter options")
    @Suppress("unused")
    fun searchBooks(params: BookSearchParameters) = Book.search(params.ids)
}

data class BookSearchParameters(val ids: List<Int>)
