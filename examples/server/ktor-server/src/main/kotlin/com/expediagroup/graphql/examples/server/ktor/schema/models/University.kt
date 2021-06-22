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

class University(val id: Int, val name: String? = null) {
    companion object {
        suspend fun search(ids: List<Int>): List<University> =
            listOf(
                University(id = 1, name = "University of Nebraska-Lincoln"),
                University(id = 2, name = "Kansas State University"),
                University(id = 3, name = "Purdue University"),
                University(id = 4, name = "Kennesaw State University"),
                University(id = 5, name = "University of Georgia")
            ).filter { ids.contains(it.id) }
    }

    fun intThatNeverComes(): Int {
        throw GraphQLException("This value will never return")
    }
}
