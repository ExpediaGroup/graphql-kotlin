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

package com.expediagroup.graphql.generator.test.integration

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import graphql.execution.DataFetcherResult
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CompletableFutureTest {

    @Test
    fun validateSchema() {
        val queries = listOf(TopLevelObject(SimpleQuery()))
        val schema = toSchema(testSchemaConfig, queries)
        assertNotNull(schema)
        assertEquals("String!", schema.queryType.getFieldDefinition("getString").type.toString())
        assertEquals("String!", schema.queryType.getFieldDefinition("getDataFetcherResultString").type.toString())
        assertEquals("[String!]!", schema.queryType.getFieldDefinition("getListString").type.toString())
    }

    class SimpleQuery {
        private val string = "hello"

        fun getString(): CompletableFuture<String> = CompletableFuture.completedFuture(string)
        fun getDataFetcherResultString(): CompletableFuture<DataFetcherResult<String>> =
            CompletableFuture.completedFuture(DataFetcherResult.newResult<String>().data(string).build())
        fun getListString(): CompletableFuture<List<String>> = CompletableFuture.completedFuture(listOf(string))
    }
}
