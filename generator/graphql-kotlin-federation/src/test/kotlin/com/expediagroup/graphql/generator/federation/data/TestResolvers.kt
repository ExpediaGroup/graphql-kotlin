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

import com.expediagroup.graphql.generator.federation.data.queries.federated.v1.Author
import com.expediagroup.graphql.generator.federation.data.queries.federated.v1.Book
import com.expediagroup.graphql.generator.federation.data.queries.federated.v1.User
import com.expediagroup.graphql.generator.federation.execution.FederatedTypePromiseResolver
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeSuspendResolver
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

internal class BookResolver : FederatedTypeSuspendResolver<Book> {
    override val typeName: String = "Book"

    override suspend fun resolve(environment: DataFetchingEnvironment, representation: Map<String, Any>): Book? {
        val book = Book(representation["id"].toString())
        representation["weight"]?.toString()?.toDoubleOrNull()?.let {
            book.weight = it
        }
        return book
    }
}

internal class UserResolver : FederatedTypeSuspendResolver<User> {
    override val typeName: String = "User"

    override suspend fun resolve(environment: DataFetchingEnvironment, representation: Map<String, Any>): User? {
        val id = representation["userId"].toString().toInt()
        val name = representation["name"].toString()
        return User(id, name)
    }
}

internal class AuthorResolver : FederatedTypePromiseResolver<Author> {

    override val typeName: String = "Author"

    override fun resolve(
        environment: DataFetchingEnvironment,
        representation: Map<String, Any>
    ): CompletableFuture<Author?> {
        return CompletableFuture.completedFuture(authors[representation["authorId"].toString().toInt()])
    }

    companion object {
        private val authors = mapOf(
            1 to Author(1, "Author 1"),
            2 to Author(2, "Author 2"),
        )
    }
}
