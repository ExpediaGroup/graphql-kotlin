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

package com.expediagroup.graphql.examples.ktor

import com.expediagroup.graphql.examples.ktor.schema.models.BATCH_BOOK_LOADER_NAME
import com.expediagroup.graphql.examples.ktor.schema.models.COURSE_LOADER_NAME
import com.expediagroup.graphql.examples.ktor.schema.models.UNIVERSITY_LOADER_NAME
import com.expediagroup.graphql.examples.ktor.schema.models.batchBookLoader
import com.expediagroup.graphql.examples.ktor.schema.models.batchCourseLoader
import com.expediagroup.graphql.examples.ktor.schema.models.batchUniversityLoader
import com.expediagroup.graphql.server.execution.DataLoaderRegistryFactory
import org.dataloader.DataLoaderRegistry

class KtorDataLoaderRegistryFactory : DataLoaderRegistryFactory {

    override fun generate(): DataLoaderRegistry {
        val registry = DataLoaderRegistry()
        registry.register(UNIVERSITY_LOADER_NAME, batchUniversityLoader)
        registry.register(COURSE_LOADER_NAME, batchCourseLoader)
        registry.register(BATCH_BOOK_LOADER_NAME, batchBookLoader)
        return registry
    }
}
