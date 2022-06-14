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

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.exceptions.InvalidSchemaTypeException
import graphql.introspection.Introspection
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class GenerateSchemaDirectivesTest : TypeTestHelper() {

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.SCHEMA])
    annotation class SchemaDirective(val arg: String)

    @SchemaDirective(arg = "foo")
    class SimpleSchema

    @SchemaDirective(arg = "foo")
    class SchemaWithProperties(val foo: String)

    @SchemaDirective(arg = "foo")
    class SchemaWithFunctions {
        fun foo(): String = TODO()
    }

    @SchemaDirective(arg = "foo")
    class SchemaWithIgnoredFields(private val foo: String) {
        @GraphQLIgnore
        fun bar(): String = TODO()
    }

    @Test
    fun `verify schema directives can be generated from schema object`() {
        val schemaDirectives = generateSchemaDirectives(generator, TopLevelObject(SimpleSchema()))
        assertEquals(1, schemaDirectives.size)
        val appliedSchemaDirective = schemaDirectives[0]
        assertEquals("schemaDirective", appliedSchemaDirective.name)
        assertEquals("foo", appliedSchemaDirective.getArgument("arg").getValue())
    }

    @Test
    fun `verify generation of schema directives will fail if schema object contains properties`() {
        assertThrows<InvalidSchemaTypeException> {
            generateSchemaDirectives(generator, TopLevelObject(SchemaWithProperties("foo")))
        }
    }

    @Test
    fun `verify generation of schema directives will fail if schema object contains functions`() {
        assertThrows<InvalidSchemaTypeException> {
            generateSchemaDirectives(generator, TopLevelObject(SchemaWithFunctions()))
        }
    }

    @Test
    fun `verify generation of schema directives when fields and properties are ignored`() {
        val schemaDirectives = generateSchemaDirectives(generator, TopLevelObject(SchemaWithIgnoredFields("foo")))
        assertEquals(1, schemaDirectives.size)
        val appliedSchemaDirective = schemaDirectives[0]
        assertEquals("schemaDirective", appliedSchemaDirective.name)
        assertEquals("foo", appliedSchemaDirective.getArgument("arg").getValue())
    }
}
