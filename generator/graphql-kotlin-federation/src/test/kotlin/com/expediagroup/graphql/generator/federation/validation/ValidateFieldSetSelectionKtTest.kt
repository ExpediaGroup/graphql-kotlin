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

package com.expediagroup.graphql.generator.federation.validation

import com.expediagroup.graphql.generator.federation.directives.EXTERNAL_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.REQUIRES_DIRECTIVE_NAME
import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateFieldSetSelectionKtTest {

    private val stubDirectiveInfo = DirectiveInfo(
        directiveName = "custom",
        fieldSet = "id",
        typeName = "Foo"
    )

    @Test
    fun `empty selection list returns no errors`() {
        val errors = mutableListOf<String>()
        validateFieldSetSelection(
            validatedDirective = stubDirectiveInfo,
            selections = emptyList(),
            fields = emptyMap(),
            errors = errors
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `selection specifies field that does not exist and returns single error`() {
        val errors = mutableListOf<String>()
        validateFieldSetSelection(
            validatedDirective = stubDirectiveInfo,
            selections = listOf(FieldSetSelection("id")),
            fields = emptyMap(),
            errors = errors
        )

        assertEquals(expected = 1, actual = errors.size)
        assertEquals(expected = "@custom(fields = \"id\") directive on Foo specifies invalid field set - field set specifies field that does not exist, field=id", actual = errors.first())
    }

    @Test
    fun `valid field definition returns no errors`() {
        val fieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(Scalars.GraphQLString)
            .build()
        val errors = mutableListOf<String>()
        validateFieldSetSelection(
            validatedDirective = stubDirectiveInfo,
            selections = listOf(FieldSetSelection("foo")),
            fields = mapOf("foo" to fieldDefinition),
            errors = errors
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `@requires directive referencing local field returns an error`() {
        val fieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(Scalars.GraphQLString)
            .build()
        val errors = mutableListOf<String>()
        validateFieldSetSelection(
            validatedDirective = DirectiveInfo(REQUIRES_DIRECTIVE_NAME, "foo", "Foo"),
            selections = listOf(FieldSetSelection("foo")),
            fields = mapOf("foo" to fieldDefinition),
            errors = errors
        )

        assertEquals(1, errors.size)
        assertEquals("@requires(fields = \"foo\") directive on Foo specifies invalid field set - @requires should reference only @external fields, field=foo", errors[0])
    }

    @Test
    fun `@requires directive referencing external field returns no error`() {
        val fieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(Scalars.GraphQLString)
            .withAppliedDirective(EXTERNAL_DIRECTIVE_TYPE.toAppliedDirective())
            .build()
        val errors = mutableListOf<String>()
        validateFieldSetSelection(
            validatedDirective = DirectiveInfo(REQUIRES_DIRECTIVE_NAME, "foo", "Foo"),
            selections = listOf(FieldSetSelection("foo")),
            fields = mapOf("foo" to fieldDefinition),
            errors = errors
        )

        assertTrue(errors.isEmpty())
    }
}
