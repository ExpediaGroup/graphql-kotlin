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

import com.expediagroup.graphql.examples.server.ktor.schema.dataloaders.BookDataLoader
import com.expediagroup.graphql.examples.server.ktor.schema.dataloaders.UniversityDataLoader
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

data class Course(
    val id: Int,
    val name: String? = null,
    val universityId: Int? = null,
    val bookIds: List<Int> = listOf()
) {
    fun university(dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<University?> {
        return if (universityId != null) {
            dataFetchingEnvironment.getValueFromDataLoader(UniversityDataLoader.dataLoaderName, universityId)
        } else CompletableFuture.completedFuture(null)
    }

    fun books(dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<List<Book>> {
        return dataFetchingEnvironment.getValueFromDataLoader(BookDataLoader.dataLoaderName, bookIds)
    }

    companion object {
        fun search(ids: List<Int>): List<Course> {
            return listOf(
                Course(id = 1, name = "Biology 101", universityId = 1, bookIds = listOf(1, 2)),
                Course(id = 2, name = "Cultural Anthropology", universityId = 1),
                Course(id = 3, name = "Computer Science 101", universityId = 1, bookIds = listOf(3, 4)),
                Course(id = 4, name = "Computer Science 101", universityId = 3, bookIds = listOf(3, 4))
            ).filter { ids.contains(it.id) }
        }
    }
}
