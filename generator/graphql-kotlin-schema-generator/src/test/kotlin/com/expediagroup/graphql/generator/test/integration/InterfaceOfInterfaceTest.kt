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
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.extensions.deepName
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import graphql.schema.GraphQLInterfaceType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class InterfaceOfInterfaceTest {

    @Test
    fun `interface of interface`() {
        val queries = listOf(TopLevelObject(InterfaceOfInterfaceQuery()))
        val schema = toSchema(queries = queries, config = testSchemaConfig)
        assertEquals(expected = 2, actual = schema.queryType.fieldDefinitions.size)

        val implementation = schema.getObjectType("MyClass")
        assertNotNull(implementation)
        val interfaces = implementation.interfaces
        assertEquals(2, interfaces.size)
        assertNotNull(interfaces.firstOrNull { it.name == "FirstLevel" })
        assertNotNull(interfaces.firstOrNull { it.name == "SecondLevel" })

        val secondLevelInterface = schema.getType("SecondLevel") as? GraphQLInterfaceType
        assertNotNull(secondLevelInterface)
        val interfaceOfInterface = secondLevelInterface.interfaces
        assertEquals(1, interfaceOfInterface.size)
        assertNotNull(interfaceOfInterface.firstOrNull { it.name == "FirstLevel" })

        val firstLevelInterface = schema.getType("FirstLevel") as? GraphQLInterfaceType
        assertNotNull(firstLevelInterface)
    }

    @Test
    fun `ignore class and use interface as type`() {
        val queries = listOf(TopLevelObject(InterfaceOfInterfaceQuery()))
        val schema = toSchema(queries = queries, config = testSchemaConfig)

        // The ignored class should not be in the schema at all
        assertNull(schema.getType("IgnoredClass"))

        assertEquals(expected = 2, actual = schema.queryType.fieldDefinitions.size)
        val queryField = assertNotNull(schema.queryType.getFieldDefinition("getIgnoredClass"))
        assertEquals("SecondLevel!", queryField.type.deepName)
    }

    interface FirstLevel {
        val id: String
    }

    interface SecondLevel : FirstLevel {
        val name: String
    }

    class MyClass(override val id: String, override val name: String) : SecondLevel

    class InterfaceOfInterfaceQuery {
        fun getClass() = MyClass(id = "1", name = "fooBar")
        fun getIgnoredClass(): SecondLevel = IgnoredClass(id = "2", name = "baz")
    }

    @GraphQLIgnore
    class IgnoredClass(
        override val id: String,
        override val name: String
    ) : SecondLevel
}
