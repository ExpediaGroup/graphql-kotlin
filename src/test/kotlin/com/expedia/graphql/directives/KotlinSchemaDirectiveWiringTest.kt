package com.expedia.graphql.directives

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
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KotlinSchemaDirectiveWiringTest {

    private lateinit var mockWiring: KotlinSchemaDirectiveWiring

    @BeforeEach
    fun setUp() {
        mockWiring = spyk(object: KotlinSchemaDirectiveWiring {})
    }

    @Test
    fun `wireOnEnvironment with no matching element returns the type back`() {
        val mockElement: GraphQLDirectiveContainer = mockk()
        val mockEnvironment = KotlinSchemaDirectiveEnvironment(mockElement, mockk())

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertEquals(mockElement, result)
    }

    @Test
    fun `wireOnEnvironment with GraphQLArgument`() {
        val mockElement: GraphQLArgument = mockk()
        val mockEnvironment = KotlinSchemaDirectiveEnvironment(mockElement, mockk())

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLArgument)
        verify(exactly = 1) { mockWiring.onArgument(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLEnumType`() {
        val mockElement: GraphQLEnumType = mockk()
        val mockEnvironment = KotlinSchemaDirectiveEnvironment(mockElement, mockk())

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLEnumType)
        verify(exactly = 1) { mockWiring.onEnum(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLEnumValueDefinition`() {
        val mockElement: GraphQLEnumValueDefinition = mockk()
        val mockEnvironment = KotlinSchemaDirectiveEnvironment(mockElement, mockk())

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLEnumValueDefinition)
        verify(exactly = 1) { mockWiring.onEnumValue(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLFieldDefinition`() {
        val mockElement: GraphQLFieldDefinition = mockk()
        val mockEnvironment: KotlinFieldDirectiveEnvironment = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLFieldDefinition)
        verify(exactly = 1) { mockWiring.onField(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLInputObjectField`() {
        val mockElement: GraphQLInputObjectField = mockk()
        val mockEnvironment = KotlinSchemaDirectiveEnvironment(mockElement, mockk())

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLInputObjectField)
        verify(exactly = 1) { mockWiring.onInputObjectField(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLInputObjectType`() {
        val mockElement: GraphQLInputObjectType = mockk()
        val mockEnvironment = KotlinSchemaDirectiveEnvironment(mockElement, mockk())

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLInputObjectType)
        verify(exactly = 1) { mockWiring.onInputObjectType(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLInterfaceType`() {
        val mockElement: GraphQLInterfaceType = mockk()
        val mockEnvironment = KotlinSchemaDirectiveEnvironment(mockElement, mockk())

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLInterfaceType)
        verify(exactly = 1) { mockWiring.onInterface(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLObjectType`() {
        val mockElement: GraphQLObjectType = mockk()
        val mockEnvironment = KotlinSchemaDirectiveEnvironment(mockElement, mockk())

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLObjectType)
        verify(exactly = 1) { mockWiring.onObject(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLScalarType`() {
        val mockElement: GraphQLScalarType = mockk()
        val mockEnvironment = KotlinSchemaDirectiveEnvironment(mockElement, mockk())

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLScalarType)
        verify(exactly = 1) { mockWiring.onScalar(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLUnionType`() {
        val mockElement: GraphQLUnionType = mockk()
        val mockEnvironment = KotlinSchemaDirectiveEnvironment(mockElement, mockk())

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLUnionType)
        verify(exactly = 1) { mockWiring.onUnion(mockEnvironment) }
    }
}
