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

package com.expediagroup.graphql.directives

import com.expediagroup.graphql.exceptions.InvalidSchemaDirectiveWiringException
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class KotlinDirectiveWiringFactoryTest {

    private class UpdateDescriptionWiringKotlinSchema(
        private val newDescription: String? = null,
        private val lowerCase: Boolean = false
    ) : KotlinSchemaDirectiveWiring {
        override fun onEnum(environment: KotlinSchemaDirectiveEnvironment<GraphQLEnumType>): GraphQLEnumType =
            GraphQLEnumType.newEnum(environment.element).description(getNewDescription(environment.element.description)).build()

        override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
            val field = environment.element
            val newArguments = mutableListOf<GraphQLArgument>()
            for (argument in field.arguments) {
                newArguments.add(GraphQLArgument.newArgument(argument).description(getNewDescription(argument.description)).build())
            }
            return GraphQLFieldDefinition.newFieldDefinition(environment.element)
                .description(getNewDescription(environment.element.description))
                .arguments(newArguments)
                .build()
        }

        override fun onArgument(environment: KotlinSchemaDirectiveEnvironment<GraphQLArgument>): GraphQLArgument =
            GraphQLArgument.newArgument(environment.element).description(getNewDescription(environment.element.description)).build()

        private fun getNewDescription(original: String?) = when {
            null != newDescription -> newDescription
            lowerCase -> original?.toLowerCase()
            else -> original
        }
    }

    private val graphQLOverrideDescriptionDirective = GraphQLDirective.newDirective()
        .name("overrideDescription")
        .validLocations(
            Introspection.DirectiveLocation.ARGUMENT_DEFINITION,
            Introspection.DirectiveLocation.ENUM,
            Introspection.DirectiveLocation.FIELD_DEFINITION)
        .build()
    private val graphQLLowercaseDirective = GraphQLDirective.newDirective()
        .name("lowercase")
        .validLocation(Introspection.DirectiveLocation.FIELD_DEFINITION)
        .build()

    private class SimpleWiringFactory(overrides: Map<String, KotlinSchemaDirectiveWiring> = emptyMap()) : KotlinDirectiveWiringFactory(overrides) {
        val lowercaseWiring = UpdateDescriptionWiringKotlinSchema(lowerCase = true)

        override fun getSchemaDirectiveWiring(environment: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): KotlinSchemaDirectiveWiring? =
            if (environment.element is GraphQLFieldDefinition && environment.element.directives.any { it.name == "lowercase" }) {
                lowercaseWiring
            } else {
                null
            }
    }

    @Test
    fun `verify no action is taken if GraphQL object is not GraphQLDirectiveContainer`() {
        val original = GraphQLTypeReference("MyTypeReference")
        val actual = SimpleWiringFactory().onWire(original)

        assertEquals(original, actual)
    }

    @Test
    fun `verify no action is taken if GraphQL object does not have directive`() {
        val original = GraphQLEnumType.newEnum().name("MyEnum").build()
        val actual = SimpleWiringFactory().onWire(original)

        assertEquals(original, actual)
    }

    @Test
    fun `verify no action is taken if no wirings are specified`() {
        val original = GraphQLEnumType.newEnum().name("MyEnum").withDirective(graphQLOverrideDescriptionDirective).build()

        val modified = SimpleWiringFactory().onWire(original)
        assertEquals(original, modified)
    }

    @Test
    fun `verify directive wirings provided by wiring factory are applied on a field with directives`() {
        val original = GraphQLFieldDefinition.newFieldDefinition()
            .name("MyField")
            .type { context, visitor -> context.thisNode().accept(context, visitor) }
            .description("My Field Description")
            .withDirective(graphQLLowercaseDirective)
            .build()

        val actual = SimpleWiringFactory().onWire(graphQLSchemaElement = original, coordinates = mockk(), codeRegistry = mockk())
        assertNotEquals(original, actual)
        val updatedField = actual as? GraphQLFieldDefinition
        assertEquals("my field description", updatedField?.description)
    }

    @Test
    fun `verify manual directive wirings are applied on a field arguments with directives`() {
        val argument = GraphQLArgument.newArgument()
            .name("MyArgument")
            .description("My Argument Description")
            .type(Scalars.GraphQLString)
            .withDirective(graphQLOverrideDescriptionDirective)
            .build()

        val newDescription = "overwritten description"
        assertNotEquals(argument.description, newDescription)

        val actual = SimpleWiringFactory(overrides = mapOf("overrideDescription" to UpdateDescriptionWiringKotlinSchema(newDescription))).onWire(argument)
        assertNotEquals(argument, actual)
        val updatedArgument = actual as? GraphQLArgument
        assertEquals(newDescription, updatedArgument?.description)
    }

    @Test
    fun `verify directive wirings are applied on a field without directives if it has arguments with directives`() {
        val argument = GraphQLArgument.newArgument()
            .name("MyArgument")
            .type(Scalars.GraphQLString)
            .description("My Argument Description")
            .withDirective(graphQLOverrideDescriptionDirective)
            .build()

        val original = GraphQLFieldDefinition.newFieldDefinition()
            .name("MyField")
            .type { context, visitor -> context.thisNode().accept(context, visitor) }
            .argument(argument)
            .description("My Field Description")
            .build()

        val newDescription = "overwritten"

        val actual = SimpleWiringFactory(overrides = mapOf("overrideDescription" to UpdateDescriptionWiringKotlinSchema(newDescription)))
            .onWire(graphQLSchemaElement = original, coordinates = mockk(), codeRegistry = mockk())
        assertNotEquals(original, actual)
        val actualField = actual as? GraphQLFieldDefinition
        assertEquals(newDescription, actualField?.description)
        val actualArgument = actualField?.getArgument("MyArgument")
        assertEquals(newDescription, actualArgument?.description)
    }

    @Test
    fun `verify manual wirings take precedence over wiring factory`() {
        val original = GraphQLFieldDefinition.newFieldDefinition()
            .name("MyField")
            .type { context, visitor -> context.thisNode().accept(context, visitor) }
            .description("My Field Description")
            .withDirective(graphQLLowercaseDirective)
            .build()

        val overwrittenDescription = "overwritten"

        // reusing lower case directive that just overwrites the description
        val actual = SimpleWiringFactory(overrides = mapOf("lowercase" to UpdateDescriptionWiringKotlinSchema(overwrittenDescription)))
            .onWire(graphQLSchemaElement = original, coordinates = mockk(), codeRegistry = mockk())
        assertNotEquals(original, actual)

        val updatedField = actual as? GraphQLFieldDefinition
        assertEquals(overwrittenDescription, updatedField?.description)
    }

    @Test
    fun `verify directives are applied in order they were declared`() {
        val original = GraphQLFieldDefinition.newFieldDefinition()
            .name("MyField")
            .type { context, visitor -> context.thisNode().accept(context, visitor) }
            .description("My Field Description")
            .withDirective(graphQLOverrideDescriptionDirective)
            .withDirective(graphQLLowercaseDirective)
            .build()

        val overwrittenDescription = "OverWriTTen"
        val actual = SimpleWiringFactory(overrides = mapOf("overrideDescription" to UpdateDescriptionWiringKotlinSchema(overwrittenDescription)))
            .onWire(graphQLSchemaElement = original, coordinates = mockk(), codeRegistry = mockk())
        assertNotEquals(original, actual)

        val updatedField = actual as? GraphQLFieldDefinition
        assertEquals(overwrittenDescription.toLowerCase(), updatedField?.description)
    }

    @Test
    fun `verify exception is thrown if no coordinates or code registry is specified for the field`() {
        val myTestField = GraphQLFieldDefinition.newFieldDefinition()
            .name("MyField")
            .type { context, visitor -> context.thisNode().accept(context, visitor) }
            .description("My Field Description")
            .withDirective(graphQLLowercaseDirective)
            .build()

        assertFailsWith(InvalidSchemaDirectiveWiringException::class) {
            SimpleWiringFactory().onWire(graphQLSchemaElement = myTestField, coordinates = null, codeRegistry = null)
        }
    }

    @Test
    fun `verify exception is thrown if directive is applied on invalid location`() {
        val myTestObject = GraphQLObjectType.newObject()
            .name("MyObject")
            .description("My Object Description")
            .withDirective(graphQLOverrideDescriptionDirective)
            .build()

        assertFailsWith(InvalidSchemaDirectiveWiringException::class) {
            SimpleWiringFactory(overrides = mapOf("overrideDescription" to UpdateDescriptionWiringKotlinSchema("should fail"))).onWire(myTestObject)
        }
    }

    @Test
    fun `verify exception is thrown if invalid code registry`() {
        val myTestObject = GraphQLObjectType.newObject()
            .name("MyObject")
            .description("My Object Description")
            .withDirective(graphQLOverrideDescriptionDirective)
            .build()

        assertFailsWith(InvalidSchemaDirectiveWiringException::class) {
            SimpleWiringFactory(overrides = mapOf("overrideDescription" to UpdateDescriptionWiringKotlinSchema("should fail")))
                .onWire(graphQLSchemaElement = myTestObject, coordinates = mockk())
        }
    }
}
