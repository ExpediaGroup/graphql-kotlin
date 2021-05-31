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

package com.expediagroup.graphql.generator.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLUnionType
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class KotlinSchemaDirectiveEnvironmentTest {

    @Test
    fun `isValid checks the directive locations`() {
        val directive = graphql.schema.GraphQLDirective.newDirective()
            .name("customDirective")
            .validLocations(
                Introspection.DirectiveLocation.ARGUMENT_DEFINITION,
                Introspection.DirectiveLocation.ENUM,
                Introspection.DirectiveLocation.ENUM_VALUE,
                Introspection.DirectiveLocation.FIELD_DEFINITION,
                Introspection.DirectiveLocation.INPUT_FIELD_DEFINITION,
                Introspection.DirectiveLocation.INPUT_OBJECT,
                Introspection.DirectiveLocation.INTERFACE,
                Introspection.DirectiveLocation.OBJECT,
                Introspection.DirectiveLocation.SCALAR,
                Introspection.DirectiveLocation.UNION
            )
            .build()

        val codeRegistry = mockk<GraphQLCodeRegistry.Builder>()

        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLArgument>(), directive, codeRegistry).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLEnumType>(), directive, codeRegistry).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLEnumValueDefinition>(), directive, codeRegistry).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLFieldDefinition>(), directive, codeRegistry).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLInputObjectField>(), directive, codeRegistry).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLInputObjectType>(), directive, codeRegistry).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLInterfaceType>(), directive, codeRegistry).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLObjectType>(), directive, codeRegistry).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLScalarType>(), directive, codeRegistry).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLUnionType>(), directive, codeRegistry).isValid())
    }

    @Test
    fun `isValid returns false in an invalid directive location`() {
        val enumDirective = graphql.schema.GraphQLDirective.newDirective()
            .name("enumDirective")
            .validLocations(Introspection.DirectiveLocation.ENUM)
            .build()
        assertFalse(KotlinSchemaDirectiveEnvironment(mockk<GraphQLArgument>(), enumDirective, mockk<GraphQLCodeRegistry.Builder>()).isValid())
    }

    @Test
    fun `isValid returns false on non valid type`() {
        assertFalse(KotlinSchemaDirectiveEnvironment(mockk<GraphQLDirectiveContainer>(), mockk(), mockk<GraphQLCodeRegistry.Builder>()).isValid())
    }
}
