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

package com.expediagroup.graphql.federation.data

import com.expediagroup.graphql.federation.data.queries.federated.Book
import com.expediagroup.graphql.federation.data.queries.federated.User
import com.expediagroup.graphql.federation.execution.FederatedTypeResolver

internal class BookResolver : FederatedTypeResolver<Book> {
    override suspend fun resolve(representations: List<Map<String, Any>>): List<Book?> {
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
    override suspend fun resolve(representations: List<Map<String, Any>>): List<User?> {
        val results = mutableListOf<User?>()
        for (keys in representations) {
            val id = keys["userId"].toString().toInt()
            val name = keys["name"].toString()
            results.add(User(id, name))
        }
        return results
    }
}
