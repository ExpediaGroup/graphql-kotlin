package com.expediagroup.graphql.directives

import graphql.introspection.Introspection
import graphql.schema.GraphQLArgument
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

        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLArgument>(), directive).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLEnumType>(), directive).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLEnumValueDefinition>(), directive).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLFieldDefinition>(), directive).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLInputObjectField>(), directive).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLInputObjectType>(), directive).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLInterfaceType>(), directive).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLObjectType>(), directive).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLScalarType>(), directive).isValid())
        assertTrue(KotlinSchemaDirectiveEnvironment(mockk<GraphQLUnionType>(), directive).isValid())
    }

    @Test
    fun `isValid returns false in an invalid directive location`() {
        val enumDirective = graphql.schema.GraphQLDirective.newDirective()
            .name("enumDirective")
            .validLocations(Introspection.DirectiveLocation.ENUM)
            .build()
        assertFalse(KotlinSchemaDirectiveEnvironment(mockk<GraphQLArgument>(), enumDirective).isValid())
    }

    @Test
    fun `isValid returns false on non valid type`() {
        assertFalse(KotlinSchemaDirectiveEnvironment(mockk<GraphQLDirectiveContainer>(), mockk()).isValid())
    }
}
