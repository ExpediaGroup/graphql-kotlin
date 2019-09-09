/*
 * Copyright 2019 Expedia Group, Inc.
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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.test.utils.CustomDirective
import com.expediagroup.graphql.test.utils.SimpleDirective
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class EnumBuilderTest : TypeTestHelper() {

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLDescription("MyTestEnum description")
    @SimpleDirective
    private enum class MyTestEnum {
        @GraphQLDescription("enum 'ONE' description")
        @SimpleDirective
        ONE,

        @GraphQLDescription("enum 'TWO' description")
        @Deprecated("Deprecated enum value")
        TWO,

        @SimpleDirective
        @CustomDirective("foo bar")
        @Deprecated("THREE is out", replaceWith = ReplaceWith("TWO"))
        THREE
    }

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLName("MyTestEnumRenamed")
    private enum class MyTestEnumCustomName

    lateinit var builder: EnumBuilder

    override fun beforeTest() {
        super.beforeTest()
        builder = EnumBuilder(generator)
    }

    @Test
    fun enumType() {
        val actual = builder.enumType(MyTestEnum::class)
        assertEquals(expected = 3, actual = actual.values.size)
        assertEquals(expected = "MyTestEnum", actual = actual.name)
        assertEquals(expected = "ONE", actual = actual.values[0].value)
        assertEquals(expected = "TWO", actual = actual.values[1].value)
        assertEquals(expected = "THREE", actual = actual.values[2].value)
    }

    @Test
    fun `Custom name on enum class`() {
        val gqlEnum = assertNotNull(builder.enumType(MyTestEnumCustomName::class))
        assertEquals("MyTestEnumRenamed", gqlEnum.name)
    }

    @Test
    fun `Description on enum class and values`() {
        val gqlEnum = assertNotNull(builder.enumType(MyTestEnum::class))
        assertEquals("MyTestEnum description", gqlEnum.description)

        assertEquals("enum 'ONE' description", assertNotNull(gqlEnum.getValue("ONE")).description)
        assertEquals("enum 'TWO' description", assertNotNull(gqlEnum.getValue("TWO")).description)
        assertNull(gqlEnum.getValue("THREE").description)
    }

    @Test
    fun `Deprecation on enum values`() {
        val gqlEnum = assertNotNull(builder.enumType(MyTestEnum::class))

        val one = assertNotNull(gqlEnum.getValue("ONE"))
        assertFalse(one.isDeprecated)
        assertNull(one.deprecationReason)

        val two = assertNotNull(gqlEnum.getValue("TWO"))
        assertTrue(two.isDeprecated)
        assertEquals("Deprecated enum value", two.deprecationReason)

        val three = assertNotNull(gqlEnum.getValue("THREE"))
        assertTrue(three.isDeprecated)
        assertEquals("THREE is out, replace with TWO", three.deprecationReason)
    }

    @Test
    fun `Enum classes can have directives`() {
        val gqlEnum = assertNotNull(builder.enumType(MyTestEnum::class))
        assertEquals(1, gqlEnum.directives.size)
        assertEquals("simpleDirective", gqlEnum.directives.first().name)
    }

    @Test
    fun `Enum values can have directives`() {
        val gqlEnum = assertNotNull(builder.enumType(MyTestEnum::class))

        val enumValuesDirectives = gqlEnum.values.last().directives
        assertEquals(3, enumValuesDirectives.size)
        assertEquals("simpleDirective", enumValuesDirectives[0].name)
        assertEquals("customName", enumValuesDirectives[1].name)
        assertEquals("deprecated", enumValuesDirectives[2].name)
    }

    @Test
    fun `Enum values can have a multiple directives`() {
        val gqlEnum = assertNotNull(builder.enumType(MyTestEnum::class))
        assertEquals(1, gqlEnum.values.first().directives.size)
        assertEquals("simpleDirective", gqlEnum.values.first().directives.first().name)
    }
}
