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

package com.expediagroup.graphql.examples.server.ktor.schema.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Contains Book Metadata, title, authorship, and references to product and content.")
data class Book(
    val id: Int,
    val title: String
) {
    @Suppress("unused")
    companion object {
        fun search(ids: List<Int>): List<Book> {
            return listOf(
                Book(id = 1, title = "Campbell Biology"),
                Book(id = 2, title = "The Cell"),
                Book(id = 3, title = "Data Structures in C++"),
                Book(id = 4, title = "The Algorithm Design Manual")
            ).filter { ids.contains(it.id) }
        }
    }
}
