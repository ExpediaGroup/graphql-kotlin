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

package com.expediagroup.graphql.generator.test.integration

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertNotNull

class RecursiveInputTest {

    @Test
    fun `Input type with a recursive argument should work`() {
        val queries = listOf(TopLevelObject(RecursiveInputQueries()))
        val schema = toSchema(testSchemaConfig, queries)
        assertNotNull(schema)
        assertNotNull(schema.getType("RecursivePerson"))
        assertNotNull(schema.getType("RecursivePersonInput"))
    }

    class RecursivePerson {
        val id: String = UUID.randomUUID().toString()
        val bestFriend: RecursivePerson = RecursivePerson()
        val friends: List<RecursivePerson> = listOf(RecursivePerson(), RecursivePerson())
    }

    class RecursiveInputQueries {
        fun getPerson() = RecursivePerson()
        fun getMultiplePeople() = listOf(RecursivePerson(), RecursivePerson())
        fun getId(inputPerson: RecursivePerson): String = inputPerson.id
        fun getMultipleIds(people: ArrayList<RecursivePerson>): List<String> = people.map { it.id }
    }
}
