package com.expediagroup.graphql.generator.federation.validation

import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import graphql.Scalars
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateFieldSelectionKtTest {
    private val stubDirectiveInfo = DirectiveInfo(
        directiveName = "custom",
        fieldSet = "foo",
        typeName = "Foo"
    )

    @Test
    fun `selection set on GraphQLScalar returns an error`() {
        val errors = mutableListOf<String>()
        validateFieldSelection(
            validatedDirective = stubDirectiveInfo,
            selection = FieldSetSelection("foo", mutableListOf(FieldSetSelection("bar"))),
            targetType = Scalars.GraphQLString,
            errors = errors
        )

        assertEquals(1, errors.size)
        assertEquals("@custom(fields = \"foo\") directive on Foo specifies invalid field set - field set specifies selection set on a leaf node, field=foo", errors[0])
    }

    @Test
    fun `selection set on GraphQLEnum returns an error`() {
        val errors = mutableListOf<String>()
        val enum = GraphQLEnumType.newEnum()
            .name("Bar")
            .value("ONE")
            .build()
        validateFieldSelection(
            validatedDirective = stubDirectiveInfo,
            selection = FieldSetSelection("foo", mutableListOf(FieldSetSelection("bar"))),
            targetType = enum,
            errors = errors
        )

        assertEquals(1, errors.size)
        assertEquals("@custom(fields = \"foo\") directive on Foo specifies invalid field set - field set specifies selection set on a leaf node, field=foo", errors[0])
    }

    @Test
    fun `selection set on GraphQLUnion returns an error`() {
        val errors = mutableListOf<String>()
        val union = GraphQLUnionType.newUnionType()
            .name("CustomUnion")
            .possibleType(
                GraphQLObjectType.newObject()
                    .name("Bar")
                    .field(
                        GraphQLFieldDefinition.newFieldDefinition()
                            .name("bar")
                            .type(Scalars.GraphQLString)
                            .build()
                    )
                    .build()
            )
            .build()
        validateFieldSelection(
            validatedDirective = stubDirectiveInfo,
            selection = FieldSetSelection("foo", mutableListOf(FieldSetSelection("bar"))),
            targetType = union,
            errors = errors
        )

        assertEquals(1, errors.size)
        assertEquals("@custom(fields = \"foo\") directive on Foo specifies invalid field set - field set references GraphQLUnionType, field=foo", errors[0])
    }

    @Test
    fun `@key selection set on a List return an error`() {
        val errors = mutableListOf<String>()
        validateFieldSelection(
            validatedDirective = DirectiveInfo(KEY_DIRECTIVE_NAME, "foo", "Foo"),
            selection = FieldSetSelection("foo"),
            targetType = GraphQLList.list(Scalars.GraphQLString),
            errors = errors
        )

        assertEquals(1, errors.size)
        assertEquals("@key(fields = \"foo\") directive on Foo specifies invalid field set - field set references GraphQLList, field=foo", errors[0])
    }

    @Test
    fun `non @key selection set on a List does not return an error`() {
        val errors = mutableListOf<String>()
        validateFieldSelection(
            validatedDirective = stubDirectiveInfo,
            selection = FieldSetSelection("foo"),
            targetType = GraphQLList.list(Scalars.GraphQLString),
            errors = errors
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `@key selection set on interface return an error`() {
        val errors = mutableListOf<String>()
        val intf = GraphQLInterfaceType.newInterface()
            .name("CustomInterface")
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("bar")
                    .type(Scalars.GraphQLString)
                    .build()
            )
            .build()
        validateFieldSelection(
            validatedDirective = DirectiveInfo(KEY_DIRECTIVE_NAME, "foo", "Foo"),
            selection = FieldSetSelection("foo", mutableListOf(FieldSetSelection("bar"))),
            targetType = intf,
            errors = errors
        )

        assertEquals(1, errors.size)
        assertEquals("@key(fields = \"foo\") directive on Foo specifies invalid field set - field set references GraphQLInterfaceType, field=foo", errors[0])
    }

    @Test
    fun `empty sub-selection on interface return an error`() {
        val errors = mutableListOf<String>()
        val intf = GraphQLInterfaceType.newInterface()
            .name("CustomInterface")
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("bar")
                    .type(Scalars.GraphQLString)
                    .build()
            )
            .build()
        validateFieldSelection(
            validatedDirective = stubDirectiveInfo,
            selection = FieldSetSelection("foo"),
            targetType = intf,
            errors = errors
        )

        assertEquals(1, errors.size)
        assertEquals("@custom(fields = \"foo\") directive on Foo specifies invalid field set - foo interface does not specify selection set", errors[0])
    }

    @Test
    fun `valid sub-selection on interface does not return an error`() {
        val errors = mutableListOf<String>()
        val intf = GraphQLInterfaceType.newInterface()
            .name("CustomInterface")
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("bar")
                    .type(Scalars.GraphQLString)
                    .build()
            )
            .build()
        validateFieldSelection(
            validatedDirective = stubDirectiveInfo,
            selection = FieldSetSelection("foo", mutableListOf(FieldSetSelection("bar"))),
            targetType = intf,
            errors = errors
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `empty sub-selection set on object returns an error`() {
        val errors = mutableListOf<String>()
        val obj = GraphQLObjectType.newObject()
            .name("Custom")
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("bar")
                    .type(Scalars.GraphQLString)
                    .build()
            )
            .build()
        validateFieldSelection(
            validatedDirective = stubDirectiveInfo,
            selection = FieldSetSelection("foo"),
            targetType = obj,
            errors = errors
        )

        assertEquals(1, errors.size)
        assertEquals("@custom(fields = \"foo\") directive on Foo specifies invalid field set - foo object does not specify selection set", errors[0])
    }

    @Test
    fun `valid sub-selection on object does not return an error`() {
        val errors = mutableListOf<String>()
        val obj = GraphQLObjectType.newObject()
            .name("Custom")
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("bar")
                    .type(Scalars.GraphQLString)
                    .build()
            )
            .build()
        validateFieldSelection(
            validatedDirective = stubDirectiveInfo,
            selection = FieldSetSelection("foo", mutableListOf(FieldSetSelection("bar"))),
            targetType = obj,
            errors = errors
        )

        assertTrue(errors.isEmpty())
    }
}
