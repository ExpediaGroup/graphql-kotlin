/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLEnumValueException
import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLNameException
import com.expediagroup.graphql.generator.test.utils.CustomDirective
import com.expediagroup.graphql.generator.test.utils.SimpleDirective
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GenerateEnumTest : TypeTestHelper() {

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLDescription("MyTestEnum description")
    @SimpleDirective
    enum class MyTestEnum {
        @GraphQLDescription("enum 'ONE' description")
        @SimpleDirective
        ONE,

        @GraphQLDescription("enum 'TWO' description")
        @Deprecated("Deprecated enum value")
        TWO,

        @SimpleDirective
        @CustomDirective("foo bar")
        @Deprecated("THREE is out", replaceWith = ReplaceWith("TWO"))
        THREE,

        @GraphQLName("customFour")
        FOUR
    }

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLName("MyTestEnumRenamed")
    enum class MyTestEnumCustomName

    enum class `Invalid$EnumName`

    enum class InvalidEnum {
        FOO, FOO_BAR, `FOO$BAR`
    }

    @Test
    fun enumType() {
        val actual = generateEnum(generator, MyTestEnum::class)
        assertEquals(expected = 4, actual = actual.values.size)
        assertEquals(expected = "MyTestEnum", actual = actual.name)
        assertEquals(expected = "ONE", actual = actual.values[0].value)
        assertEquals(expected = "TWO", actual = actual.values[1].value)
        assertEquals(expected = "THREE", actual = actual.values[2].value)
        assertEquals(expected = "customFour", actual = actual.values[3].value)
    }

    @Test
    fun `Custom name on enum class`() {
        val gqlEnum = assertNotNull(generateEnum(generator, MyTestEnumCustomName::class))
        assertEquals("MyTestEnumRenamed", gqlEnum.name)
    }

    @Test
    fun `Description on enum class and values`() {
        val gqlEnum = assertNotNull(generateEnum(generator, MyTestEnum::class))
        assertEquals("MyTestEnum description", gqlEnum.description)

        assertEquals("enum 'ONE' description", assertNotNull(gqlEnum.getValue("ONE")).description)
        assertEquals("enum 'TWO' description", assertNotNull(gqlEnum.getValue("TWO")).description)
        assertNull(gqlEnum.getValue("THREE").description)
    }

    @Test
    fun `Deprecation on enum values`() {
        val gqlEnum = assertNotNull(generateEnum(generator, MyTestEnum::class))

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
        val gqlEnum = assertNotNull(generateEnum(generator, MyTestEnum::class))
        assertEquals(1, gqlEnum.appliedDirectives.size)
        assertEquals("simpleDirective", gqlEnum.appliedDirectives.first().name)
    }

    @Test
    fun `Enum values can have directives`() {
        val gqlEnum = assertNotNull(generateEnum(generator, MyTestEnum::class))

        val enumValuesDirectives = gqlEnum.values.find { it.name == "THREE" }?.appliedDirectives
        assertNotNull(enumValuesDirectives)
        assertEquals(3, enumValuesDirectives.size)
        assertEquals("simpleDirective", enumValuesDirectives[0].name)
        assertEquals("customName", enumValuesDirectives[1].name)
        assertEquals("deprecated", enumValuesDirectives[2].name)
    }

    @Test
    fun `Enum values can have a multiple directives`() {
        val gqlEnum = assertNotNull(generateEnum(generator, MyTestEnum::class))
        assertEquals(1, gqlEnum.values.first().appliedDirectives.size)
        assertEquals("simpleDirective", gqlEnum.values.first().appliedDirectives.first().name)
    }

    @Test
    fun `Enum values can have a custom name`() {
        val gqlEnum = assertNotNull(generateEnum(generator, MyTestEnum::class))

        assertNotNull(gqlEnum.getValue("customFour"))
    }

    @Test
    fun `Enum generation will fail if it has invalid name`() {
        assertFailsWith<InvalidGraphQLNameException> {
            generateEnum(generator, `Invalid$EnumName`::class)
        }
    }

    @Test
    fun `Enum generation will fail if it has invalid value`() {
        assertFailsWith<InvalidGraphQLEnumValueException> {
            generateEnum(generator, `InvalidEnum`::class)
        }
    }
}
