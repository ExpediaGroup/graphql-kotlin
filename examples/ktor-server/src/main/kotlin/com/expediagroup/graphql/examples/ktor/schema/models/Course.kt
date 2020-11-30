/**
 * Copyright 2020 Expedia, Inc
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
package com.expediagroup.graphql.examples.ktor.schema.models

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import java.util.concurrent.CompletableFuture.supplyAsync

const val COURSE_LOADER_NAME = "COURSE_LOADER"

val batchCourseLoader = DataLoader<Long, Course?> { ids ->
    supplyAsync {
        runBlocking { Course.search(ids).toMutableList() }
    }
}

data class Course(
    val id: Long,
    val name: String? = null,
    val universityId: Long? = null,
    val bookIds: List<Long> = listOf()
) {
    suspend fun university(dataFetchingEnvironment: DataFetchingEnvironment): University? {
        return dataFetchingEnvironment.getDataLoader<Long, University>(UNIVERSITY_LOADER_NAME)
            .load(universityId).await()
    }

    suspend fun books(dataFetchingEnvironment: DataFetchingEnvironment): List<Book>? {
        val books = dataFetchingEnvironment.getDataLoader<List<Long>, List<Book>>(BATCH_BOOK_LOADER_NAME)
            .load(bookIds).await()
        return books
    }

    companion object {
        suspend fun search(ids: List<Long>): List<Course> {
            return listOf(
                Course(id = 1, name = "Biology 101", universityId = 1, bookIds = listOf(1, 2)),
                Course(id = 2, name = "Cultural Anthropology", universityId = 1),
                Course(id = 3, name = "Computer Science 101", universityId = 1, bookIds = listOf(3, 4)),
                Course(id = 4, name = "Computer Science 101", universityId = 3, bookIds = listOf(3, 4))
            ).filter { ids.contains(it.id) }
        }
    }
}
