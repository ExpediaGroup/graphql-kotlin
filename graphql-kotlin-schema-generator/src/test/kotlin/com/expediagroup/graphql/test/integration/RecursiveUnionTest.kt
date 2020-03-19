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

package com.expediagroup.graphql.test.integration

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.testGenerator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RecursiveUnionTest {

    @Test
    fun recursiveUnion() {
        val queries = listOf(TopLevelObject(RecursiveUnionQuery()))
        val schema = testGenerator.generateSchema(queries = queries)
        assertEquals(1, schema.queryType.fieldDefinitions.size)
        val field = schema.queryType.fieldDefinitions.first()
        assertEquals("getRoot", field.name)
    }

    class RecursiveUnionQuery {
        fun getRoot() = RecursiveUnionA()
    }

    interface SomeUnion

    class RecursiveUnionA : SomeUnion {
        fun getB() = RecursiveUnionB()
    }

    class RecursiveUnionB : SomeUnion {
        fun getA() = RecursiveUnionA()
    }
}
