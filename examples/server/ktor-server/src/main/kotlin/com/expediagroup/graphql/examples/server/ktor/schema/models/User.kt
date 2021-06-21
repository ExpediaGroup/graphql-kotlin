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

import graphql.GraphQLException

data class User(
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val universityId: Int?,
    val isAdmin: Boolean = false
) {
    suspend fun university(): University? {
        universityId ?: return null
        return University.search(listOf(universityId))[0]
    }

    fun intThatNeverComes(): Int =
        throw GraphQLException("This value will never return")
}
