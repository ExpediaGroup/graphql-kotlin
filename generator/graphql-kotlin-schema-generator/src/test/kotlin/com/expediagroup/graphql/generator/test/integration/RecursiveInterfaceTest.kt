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
import kotlin.test.assertEquals

class RecursiveInterfaceTest {

    @Test
    fun recursiveInterface() {
        val queries = listOf(TopLevelObject(RecursiveInterfaceQuery()))
        val schema = toSchema(queries = queries, config = testSchemaConfig)
        assertEquals(1, schema.queryType.fieldDefinitions.size)
        val field = schema.queryType.fieldDefinitions.first()
        assertEquals("getRoot", field.name)
    }

    @Test
    fun `interface with self field`() {
        val queries = listOf(TopLevelObject(InterfaceWithSelfFieldQuery()))
        val schema = toSchema(queries = queries, config = testSchemaConfig)
        assertEquals(1, schema.queryType.fieldDefinitions.size)
        val field = schema.queryType.fieldDefinitions.first()
        assertEquals("getInterface", field.name)
    }
}

class RecursiveInterfaceQuery {
    fun getRoot() = RecursiveClassA()
}

class InterfaceWithSelfFieldQuery {
    fun getInterface() = InterfaceWithSelfFieldB()
}

interface SomeInterfaceWithId {
    val id: String
}

interface InterfaceWithSelfField {
    val parent: InterfaceWithSelfField?
}

class RecursiveClassA : SomeInterfaceWithId {
    override val id = "A"
    fun getB() = RecursiveClassB()
}

class RecursiveClassB : SomeInterfaceWithId {
    override val id = "B"
    fun getA() = RecursiveClassA()
}

class InterfaceWithSelfFieldA : InterfaceWithSelfField {
    override val parent: InterfaceWithSelfField? = null
}

class InterfaceWithSelfFieldB : InterfaceWithSelfField {
    override val parent = InterfaceWithSelfFieldA()
}
