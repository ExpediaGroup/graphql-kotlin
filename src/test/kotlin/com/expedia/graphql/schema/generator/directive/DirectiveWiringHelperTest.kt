package com.expedia.graphql.schema.generator.directive

import com.expedia.graphql.schema.exceptions.GraphQLKotlinException
import graphql.Scalars
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeVisitor
import graphql.schema.GraphQLUnionType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.WiringFactory
import graphql.util.TraversalControl
import graphql.util.TraverserContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

internal class DirectiveWiringHelperTest {

    private class DescriptionWiring(private val description: String) : SchemaDirectiveWiring {
        override fun onEnum(environment: SchemaDirectiveWiringEnvironment<GraphQLEnumType>?): GraphQLEnumType =
            GraphQLEnumType.newEnum(environment?.element).description(description).build()

        override fun onField(environment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>?): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition(environment?.element).description(description).build()

        override fun onArgument(environment: SchemaDirectiveWiringEnvironment<GraphQLArgument>?): GraphQLArgument =
            GraphQLArgument.newArgument(environment?.element).description(description).build()
    }

    private class InvalidWiring : SchemaDirectiveWiring {
        override fun onEnum(environment: SchemaDirectiveWiringEnvironment<GraphQLEnumType>?): GraphQLEnumType =
            throw GraphQLKotlinException()

        override fun onInputObjectType(environment: SchemaDirectiveWiringEnvironment<GraphQLInputObjectType>?): GraphQLInputObjectType =
            throw GraphQLKotlinException()

        override fun onUnion(environment: SchemaDirectiveWiringEnvironment<GraphQLUnionType>?): GraphQLUnionType =
            throw GraphQLKotlinException()

        override fun onInputObjectField(environment: SchemaDirectiveWiringEnvironment<GraphQLInputObjectField>?): GraphQLInputObjectField =
            throw GraphQLKotlinException()

        override fun onArgument(environment: SchemaDirectiveWiringEnvironment<GraphQLArgument>?): GraphQLArgument =
            throw GraphQLKotlinException()

        override fun onInterface(environment: SchemaDirectiveWiringEnvironment<GraphQLInterfaceType>?): GraphQLInterfaceType =
            throw GraphQLKotlinException()

        override fun onEnumValue(environment: SchemaDirectiveWiringEnvironment<GraphQLEnumValueDefinition>?): GraphQLEnumValueDefinition =
            throw GraphQLKotlinException()

        override fun onObject(environment: SchemaDirectiveWiringEnvironment<GraphQLObjectType>?): GraphQLObjectType =
            throw GraphQLKotlinException()

        override fun onScalar(environment: SchemaDirectiveWiringEnvironment<GraphQLScalarType>?): GraphQLScalarType =
            throw GraphQLKotlinException()

        override fun onField(environment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>?): GraphQLFieldDefinition =
            throw GraphQLKotlinException()
    }

    private val graphQLDirective = GraphQLDirective.newDirective().name("MyDirective").build()

    private class SimpleWiringFactory : WiringFactory

    @Test
    fun `An enum with no directives`() {
        val enum = GraphQLEnumType.newEnum().name("MyEnum").build()
        val actual = DirectiveWiringHelper(SimpleWiringFactory()).onWire(enum)
        val directives = (actual as? GraphQLEnumType)?.directives
        assertEquals(expected = 0, actual = directives?.size)
    }

    @Test
    fun `A list of enums with no directives `() {
        val enum = GraphQLEnumType.newEnum().name("MyEnum").build()
        val list = GraphQLList(enum)
        val actual = DirectiveWiringHelper(SimpleWiringFactory()).onWire(list)
        val directives = ((actual as? GraphQLList)?.wrappedType as? GraphQLEnumType)?.directives
        assertEquals(expected = 0, actual = directives?.size)
    }

    @Test
    fun `An enum with directives and no wiring`() {
        val enum = GraphQLEnumType.newEnum().name("MyEnum").withDirective(graphQLDirective).build()
        val actual = DirectiveWiringHelper(SimpleWiringFactory()).onWire(enum)
        val directives = (actual as? GraphQLEnumType)?.directives
        assertEquals(expected = 1, actual = directives?.size)
    }

    @Test
    fun `An enum with directives with manual wiring`() {
        val enum = GraphQLEnumType.newEnum().name("MyEnum").withDirective(graphQLDirective).build()
        val description = "foo bar"
        val manualWiringMap = mapOf("MyDirective" to DescriptionWiring(description))
        val actual = DirectiveWiringHelper(SimpleWiringFactory(), manualWiringMap).onWire(enum)
        val enumType = actual as? GraphQLEnumType
        val directives = enumType?.directives
        assertEquals(expected = 1, actual = directives?.size)
        assertEquals(expected = description, actual = enumType?.description)
    }

    @Test
    fun `A custum type with directives with manual wiring but no matching wiring type`() {
        class MyCustomType : GraphQLType, GraphQLDirectiveContainer {
            override fun getName(): String = "MyCustomType"

            override fun getDirectives(): MutableList<GraphQLDirective> = mutableListOf(graphQLDirective)

            override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl = context.thisNode().accept(context, visitor)
        }

        val manualWiringMap = mapOf("MyDirective" to InvalidWiring())

        val actual = DirectiveWiringHelper(SimpleWiringFactory(), manualWiringMap).onWire(MyCustomType())
        val enumType = actual as? MyCustomType
        val directives = enumType?.directives
        assertEquals(expected = 1, actual = directives?.size)
    }

    @Test
    fun `An enum with directives with invalid wiring and matching wiring type`() {
        val enum = GraphQLEnumType.newEnum().name("MyEnum").withDirective(graphQLDirective).build()

        val manualWiringMap = mapOf("MyDirective" to InvalidWiring())

        assertFailsWith(GraphQLKotlinException::class) {
            DirectiveWiringHelper(SimpleWiringFactory(), manualWiringMap).onWire(enum)
        }
    }

    @Test
    fun `An enum with directives with basic wiring and matching wiring type`() {
        val enum = GraphQLEnumType.newEnum().name("MyEnum").withDirective(graphQLDirective).build()
        val description = "test"

        class MyWiringFactory : WiringFactory {
            override fun providesSchemaDirectiveWiring(environment: SchemaDirectiveWiringEnvironment<*>?) = true

            override fun getSchemaDirectiveWiring(environment: SchemaDirectiveWiringEnvironment<*>?) = DescriptionWiring(description)
        }

        val actual = DirectiveWiringHelper(MyWiringFactory()).onWire(enum)
        val enumType = actual as? GraphQLEnumType
        val directives = enumType?.directives
        assertEquals(expected = 1, actual = directives?.size)
        assertEquals(expected = description, actual = enumType?.description)
    }

    @Test
    fun `An enum with directives with no wiring`() {
        val enum = GraphQLEnumType.newEnum().name("MyEnum").withDirective(graphQLDirective).build()
        val actual = DirectiveWiringHelper(SimpleWiringFactory()).onWire(enum)
        val directives = (actual as? GraphQLEnumType)?.directives
        assertEquals(expected = 1, actual = directives?.size)
    }

    @Test
    fun `An field with directives and arguments with no directives`() {
        val arugment = GraphQLArgument.newArgument()
            .name("MyArgument")
            .type(Scalars.GraphQLString)
            .build()

        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("MyField")
            .type(
                object : GraphQLOutputType {
                    override fun getName(): String = "GraphQLField"

                    override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl = context.thisNode().accept(context, visitor)
                }
            )
            .argument(arugment)
            .withDirective(graphQLDirective)
            .build()

        val description = "arguments"
        val manualWiringMap = mapOf("MyDirective" to DescriptionWiring(description))

        val actual = DirectiveWiringHelper(SimpleWiringFactory(), manualWiringMap).onWire(field)
        val fieldDefinition = actual as? GraphQLFieldDefinition
        assertEquals(expected = 1, actual = fieldDefinition?.directives?.size)
        assertEquals(expected = description, actual = fieldDefinition?.description)
        assertNull(fieldDefinition?.arguments?.first()?.description)
    }

    @Test
    fun `An field with arguments that has directives`() {
        val arugment = GraphQLArgument.newArgument()
            .name("MyArgument")
            .type(Scalars.GraphQLString)
            .withDirective(graphQLDirective)
            .build()

        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("MyField")
            .type(
                    object : GraphQLOutputType {
                        override fun getName(): String = "GraphQLField"

                        override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl = context.thisNode().accept(context, visitor)
                    }
            )
            .argument(arugment)
            .build()

        val description = "arguments"
        val manualWiringMap = mapOf("MyDirective" to DescriptionWiring(description))

        val actual = DirectiveWiringHelper(SimpleWiringFactory(), manualWiringMap).onWire(field)
        val fieldDefinition = actual as? GraphQLFieldDefinition
        assertEquals(expected = 0, actual = fieldDefinition?.directives?.size)
        assertEquals(expected = description, actual = fieldDefinition?.description)
        assertNull(fieldDefinition?.arguments?.first()?.description)
    }
}
