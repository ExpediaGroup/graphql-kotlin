package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.CouldNotCastGraphQLType
import com.expedia.graphql.exceptions.NestingNonNullTypeException
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeVisitor
import graphql.schema.GraphQLUnionType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.util.TraversalControl
import graphql.util.TraverserContext
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.reflect.KType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("Detekt.LargeClass")
internal class GraphQLExtensionsTest {

    private class BasicType : GraphQLType {
        override fun getName() = "BasicType"

        override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl =
            context.thisNode().accept(context, visitor)
    }

    private val basicType = BasicType()
    private val mockDirective: GraphQLDirective = mockk()

    @Test
    fun `wrapInNonNull twice throws exception`() {
        val nonNull = GraphQLNonNull(basicType)
        val mockKType: KType = mockk()

        assertFailsWith(NestingNonNullTypeException::class) {
            nonNull.wrapInNonNull(mockKType)
        }
    }

    @Test
    fun `wrapInNonNull with null kotlin type does nothing`() {
        val mockKType: KType = mockk()
        every { mockKType.isMarkedNullable } returns true

        assertFalse(basicType.wrapInNonNull(mockKType) is GraphQLNonNull)
        assertEquals(expected = basicType, actual = basicType.wrapInNonNull(mockKType))
    }

    @Test
    fun `wrapInNonNull with non-nullable kotlin type wraps`() {
        val mockKType: KType = mockk()
        every { mockKType.isMarkedNullable } returns false

        assertTrue(basicType.wrapInNonNull(mockKType) is GraphQLNonNull)
    }

    @Test
    fun `GraphQLDirectiveContainer with no directives`() {
        val container: GraphQLDirectiveContainer = mockk()
        every { container.directives } returns emptyList()

        assertTrue(container.getAllDirectives().isEmpty())
    }

    @Test
    fun `GraphQLDirectiveContainer with one directive`() {
        val container: GraphQLDirectiveContainer = mockk()
        every { container.directives } returns listOf(mockDirective)

        assertEquals(expected = 1, actual = container.getAllDirectives().size)
    }

    @Test
    fun `GraphQLFieldDefinition with one directive and no arguments`() {
        val container: GraphQLFieldDefinition = mockk()
        every { container.directives } returns listOf(mockDirective)
        every { container.arguments } returns emptyList()

        assertEquals(expected = 1, actual = container.getAllDirectives().size)
    }

    @Test
    fun `GraphQLFieldDefinition with one directive and argument with no directives`() {
        val mockArgument: GraphQLArgument = mockk()
        every { mockArgument.directives } returns emptyList()

        val container: GraphQLFieldDefinition = mockk()
        every { container.directives } returns listOf(mockDirective)
        every { container.arguments } returns listOf(mockArgument)

        assertEquals(expected = 1, actual = container.getAllDirectives().size)
    }

    @Test
    fun `GraphQLFieldDefinition with one directive and argument with two directives`() {
        val mockArgument: GraphQLArgument = mockk()
        every { mockArgument.directives } returns listOf(mockDirective, mockDirective)

        val container: GraphQLFieldDefinition = mockk()
        every { container.directives } returns listOf(mockDirective)
        every { container.arguments } returns listOf(mockArgument)

        assertEquals(expected = 3, actual = container.getAllDirectives().size)
    }

    @Test
    fun `wireOnEnvironment with no matching element returns the type back`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLDirectiveContainer> = mockk()
        val mockElement: GraphQLDirectiveContainer = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertEquals(mockElement, result)
    }

    @Test
    fun `wireOnEnvironment with GraphQLArgument`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLArgument> = mockk()
        val mockElement: GraphQLArgument = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLArgument)
        verify(exactly = 1) { mockWiring.onArgument(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLEnumType`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLEnumType> = mockk()
        val mockElement: GraphQLEnumType = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLEnumType)
        verify(exactly = 1) { mockWiring.onEnum(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLEnumValueDefinition`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLEnumValueDefinition> = mockk()
        val mockElement: GraphQLEnumValueDefinition = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLEnumValueDefinition)
        verify(exactly = 1) { mockWiring.onEnumValue(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLFieldDefinition`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> = mockk()
        val mockElement: GraphQLFieldDefinition = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLFieldDefinition)
        verify(exactly = 1) { mockWiring.onField(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLInputObjectField`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLInputObjectField> = mockk()
        val mockElement: GraphQLInputObjectField = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLInputObjectField)
        verify(exactly = 1) { mockWiring.onInputObjectField(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLInputObjectType`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLInputObjectType> = mockk()
        val mockElement: GraphQLInputObjectType = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLInputObjectType)
        verify(exactly = 1) { mockWiring.onInputObjectType(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLInterfaceType`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLInterfaceType> = mockk()
        val mockElement: GraphQLInterfaceType = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLInterfaceType)
        verify(exactly = 1) { mockWiring.onInterface(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLObjectType`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLObjectType> = mockk()
        val mockElement: GraphQLObjectType = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLObjectType)
        verify(exactly = 1) { mockWiring.onObject(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLScalarType`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLScalarType> = mockk()
        val mockElement: GraphQLScalarType = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLScalarType)
        verify(exactly = 1) { mockWiring.onScalar(mockEnvironment) }
    }

    @Test
    fun `wireOnEnvironment with GraphQLUnionType`() {
        val mockWiring = spyk<SchemaDirectiveWiring>()
        val mockEnvironment: SchemaDirectiveWiringEnvironment<GraphQLUnionType> = mockk()
        val mockElement: GraphQLUnionType = mockk()
        every { mockEnvironment.element } returns mockElement

        val result = mockWiring.wireOnEnvironment(mockEnvironment)
        assertTrue(result is GraphQLUnionType)
        verify(exactly = 1) { mockWiring.onUnion(mockEnvironment) }
    }

    @Test
    fun `safeCast valid type passes`() {
        val type: GraphQLType = GraphQLInterfaceType("name", "description", emptyList(), mockk())

        val castedType = type.safeCast<GraphQLInterfaceType>()
        assertEquals("name", castedType.name)
    }

    @Test
    fun `safeCast valid type to the wrong type fails`() {
        val type: GraphQLType = GraphQLObjectType("name", "description", emptyList(), emptyList())

        assertFailsWith(CouldNotCastGraphQLType::class) {
            type.safeCast<GraphQLInterfaceType>()
        }
    }
}
