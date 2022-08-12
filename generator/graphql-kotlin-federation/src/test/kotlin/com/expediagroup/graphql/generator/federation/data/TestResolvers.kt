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

package com.expediagroup.graphql.generator.federation.data

import com.expediagroup.graphql.generator.federation.data.queries.federated.Author
import com.expediagroup.graphql.generator.federation.data.queries.federated.Book
import com.expediagroup.graphql.generator.federation.data.queries.federated.User
import com.expediagroup.graphql.generator.federation.execution.FederatedTypePromiseResolver
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

internal class BookResolver : FederatedTypeResolver<Book> {
    override val typeName: String = "Book"

    override suspend fun resolve(environment: DataFetchingEnvironment, representations: List<Map<String, Any>>): List<Book?> {
        val results = mutableListOf<Book?>()
        for (keys in representations) {
            val book = Book(keys["id"].toString())
            keys["weight"]?.toString()?.toDoubleOrNull()?.let {
                book.weight = it
            }
            results.add(book)
        }

        return results
    }
}

internal class UserResolver : FederatedTypeResolver<User> {
    override val typeName: String = "User"

    override suspend fun resolve(environment: DataFetchingEnvironment, representations: List<Map<String, Any>>): List<User?> {
        val results = mutableListOf<User?>()
        for (keys in representations) {
            val id = keys["userId"].toString().toInt()
            val name = keys["name"].toString()
            results.add(User(id, name))
        }
        return results
    }
}

internal class AuthorResolver : FederatedTypePromiseResolver<Author> {

    override val typeName: String = "Author"

    override fun resolve(
        environment: DataFetchingEnvironment,
        representations: List<Map<String, Any>>
    ): CompletableFuture<List<Author?>> {
        val results = mutableListOf<Author?>()
        for (keys in representations) {
            results.add(authors[keys["authorId"].toString().toInt()])
        }
        return CompletableFuture.completedFuture(results)
    }

    companion object {
        private val authors = mapOf(
            1 to Author(1, "Author 1"),
            2 to Author(2, "Author 2"),
        )
    }
}
