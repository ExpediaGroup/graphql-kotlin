/*
 * Copyright 2026 Expedia, Inc
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
import com.expediagroup.graphql.generator.annotations.GraphQLDeprecated
import com.expediagroup.graphql.generator.annotations.GraphQLOneOf
import com.expediagroup.graphql.generator.annotations.GraphQLOneOfField
import com.expediagroup.graphql.generator.annotations.GraphQLOneOfFieldType
import com.expediagroup.graphql.generator.annotations.GraphQLType
import com.expediagroup.graphql.generator.exceptions.DuplicateOneOfInputFieldException
import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLOneOfUnwrappedFieldException
import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLOneOfTargetException
import com.expediagroup.graphql.generator.exceptions.MissingOneOfInputFieldAnnotationException
import com.expediagroup.graphql.generator.exceptions.NoGraphQLOneOfImplementationsException
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.generator.test.utils.SimpleDirective
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLNonNull
import graphql.schema.idl.SchemaPrinter
import org.junit.jupiter.api.Test
import kotlin.reflect.full.findParameterByName
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class GenerateArgumentOneOfTest : TypeTestHelper() {

    class OneOfArgumentTestClass {
        fun oneOfInput(input: PostElementInput): String = when (input) {
            is PostElementInput.ParagraphInput -> input.text
            is PostElementInput.BlockQuoteInput -> input.value
        }

        fun oneOfDeprecatedWrappedInput(input: DeprecatedWrappedOneOfInput): String = when (input) {
            is DeprecatedWrappedOneOfInput.DeprecatedParagraphInput -> input.text
            is DeprecatedWrappedOneOfInput.DeprecatedBlockQuoteInput -> input.value
        }

        fun oneOfScalarInput(input: UserByInput): String = when (input) {
            is UserByInput.UserByID -> input.id.value
            is UserByInput.UserByEmail -> input.email
            is UserByInput.UserByCriteria -> "${input.name} ${input.address}"
        }

        fun oneOfCustomGraphQLTypeInput(input: UserByUnWrappedWithDirectivesInput): String = when (input) {
            is UserByUnWrappedWithDirectivesInput.ById -> input.value
        }

        fun nestedOneOfInput(input: SearchTargetInput): String = input.toString()

        fun invalidOneOfInput(input: InvalidOneOfInput): String = input.toString()

        fun concreteOneOfInput(input: ConcreteOneOfInput): String = input.toString()

        fun nonSealedOneOfInput(input: NonSealedOneOfInput): String = input.toString()

        fun emptyOneOfInput(input: EmptyOneOfInput): String = input.toString()

        fun missingFieldAnnotationOneOfInput(input: MissingFieldAnnotationOneOfInput): String = input.toString()

        fun duplicateFieldNameOneOfInput(input: DuplicateFieldNameOneOfInput): String = input.toString()

        fun invalidUnwrappedFieldOneOfInput(input: InvalidUnwrappedFieldOneOfInput): String = input.toString()

        fun directedOneOfInput(input: DirectedOneOfInput): String = input.toString()

        fun privateConstructorUnwrappedFieldOneOfInput(input: PrivateConstructorUnwrappedFieldOneOfInput): String = input.toString()
    }

    @GraphQLOneOf
    sealed interface PostElementInput {
        @GraphQLOneOfField("paragraph")
        data class ParagraphInput(val text: String) : PostElementInput
        @GraphQLOneOfField("blockquote")
        data class BlockQuoteInput(val value: String, val attribution: String?, val attributionUrl: String?) : PostElementInput
    }

    @GraphQLOneOf
    sealed interface DeprecatedWrappedOneOfInput {
        @GraphQLOneOfField("paragraph")
        @GraphQLDeprecated("Deprecated")
        @GraphQLDescription("the paragraph description")
        data class DeprecatedParagraphInput(val text: String) : DeprecatedWrappedOneOfInput
        @GraphQLOneOfField("blockquote")
        data class DeprecatedBlockQuoteInput(val value: String) : DeprecatedWrappedOneOfInput
    }

    @GraphQLOneOf
    sealed interface UserByInput {
        @GraphQLOneOfField("email", GraphQLOneOfFieldType.UNWRAPPED)
        data class UserByEmail(val email: String) : UserByInput
        @GraphQLOneOfField("id", GraphQLOneOfFieldType.UNWRAPPED)
        data class UserByID(val id: ID) : UserByInput
        @GraphQLOneOfField("criteria")
        data class UserByCriteria(val name: String?, val address: String?) : UserByInput
    }

    @GraphQLOneOf
    sealed interface UserByUnWrappedWithDirectivesInput {
        @GraphQLOneOfField("id", GraphQLOneOfFieldType.UNWRAPPED)
        data class ById(
            @param:GraphQLDescription("something")
            @param:GraphQLDeprecated("Deprecated")
            @param:GraphQLType("ID")
            @param:SimpleDirective
            val value: String
        ) : UserByUnWrappedWithDirectivesInput
    }

    @GraphQLOneOf
    sealed interface SearchTargetInput {
        @GraphQLOneOfField("user", GraphQLOneOfFieldType.UNWRAPPED)
        data class User(val value: UserSelectorInput) : SearchTargetInput
        @GraphQLOneOfField("organization", GraphQLOneOfFieldType.UNWRAPPED)
        data class Organization(val value: OrganizationSelector) : SearchTargetInput
    }
    @GraphQLOneOf
    sealed interface UserSelectorInput {
        @GraphQLOneOfField("id", GraphQLOneOfFieldType.UNWRAPPED)
        data class Id(val value: ID) : UserSelectorInput
        @GraphQLOneOfField("email", GraphQLOneOfFieldType.UNWRAPPED)
        data class Email(val value: String) : UserSelectorInput
    }
    @GraphQLOneOf
    sealed interface OrganizationSelector {
        @GraphQLOneOfField("id", GraphQLOneOfFieldType.UNWRAPPED)
        data class Id(val value: ID) : OrganizationSelector
        @GraphQLOneOfField("slug", GraphQLOneOfFieldType.UNWRAPPED)
        data class Slug(val value: String) : OrganizationSelector
    }

    @GraphQLOneOf
    sealed class InvalidOneOfInput {
        @GraphQLOneOfField("something")
        data class Something(val message: String) : InvalidOneOfInput()
    }

    @GraphQLOneOf
    data class ConcreteOneOfInput(val id: ID?, val email: String?)

    @GraphQLOneOf
    interface NonSealedOneOfInput

    @GraphQLOneOf
    sealed interface EmptyOneOfInput

    @GraphQLOneOf
    sealed interface MissingFieldAnnotationOneOfInput {
        data class MissingAnnotation(val value: String) : MissingFieldAnnotationOneOfInput
    }

    @GraphQLOneOf
    sealed interface DuplicateFieldNameOneOfInput {
        @GraphQLOneOfField("duplicate")
        data class First(val value: String) : DuplicateFieldNameOneOfInput
        @GraphQLOneOfField("duplicate")
        data class Second(val value: String) : DuplicateFieldNameOneOfInput
    }

    @GraphQLOneOf
    sealed interface InvalidUnwrappedFieldOneOfInput {
        @GraphQLOneOfField("invalid", GraphQLOneOfFieldType.UNWRAPPED)
        data class Invalid(val first: String, val second: String) : InvalidUnwrappedFieldOneOfInput
    }

    @GraphQLOneOf
    @SimpleDirective
    sealed interface DirectedOneOfInput {
        @GraphQLOneOfField("value", GraphQLOneOfFieldType.UNWRAPPED)
        data class Value(val value: String) : DirectedOneOfInput
    }

    @GraphQLOneOf
    sealed interface PrivateConstructorUnwrappedFieldOneOfInput {
        @GraphQLOneOfField("invalid", GraphQLOneOfFieldType.UNWRAPPED)
        class Invalid private constructor(val value: String) : PrivateConstructorUnwrappedFieldOneOfInput
    }

    @Test
    fun `oneOf sealed interface argument object type is valid`() {
        val kParameter = OneOfArgumentTestClass::oneOfInput.findParameterByName("input")
        assertNotNull(kParameter)
        val result = generateArgument(generator, kParameter)

        assertEquals(expected = "input", actual = result.name)
        val nonNullType = assertNotNull(result.type as? GraphQLNonNull)
        val oneOfInputType = assertNotNull(nonNullType.wrappedType as? GraphQLInputObjectType)

        assertEquals(
            expected = """
                input PostElementInput @oneOf {
                  blockquote: BlockQuoteInput
                  paragraph: ParagraphInput
                }
            """.trimIndent(),
            actual = SchemaPrinter().print(oneOfInputType).trim()
        )

        val blockQuoteInputType = oneOfInputType.getField("blockquote")!!.type as GraphQLInputObjectType
        assertEquals(
            expected = """
                input BlockQuoteInput {
                  attribution: String
                  attributionUrl: String
                  value: String!
                }
            """.trimIndent(),
            actual = SchemaPrinter().print(blockQuoteInputType).trim()
        )

        val paragraphInputType = oneOfInputType.getField("paragraph")!!.type as GraphQLInputObjectType
        assertEquals(
            expected = """
                input ParagraphInput {
                  text: String!
                }
            """.trimIndent(),
            actual = SchemaPrinter().print(paragraphInputType).trim()
        )
    }

    @Test
    fun `oneOf wrapped field uses subtype deprecation annotation`() {
        val kParameter = OneOfArgumentTestClass::oneOfDeprecatedWrappedInput.findParameterByName("input")
        assertNotNull(kParameter)

        val result = generateArgument(generator, kParameter)

        val nonNullType = assertNotNull(result.type as? GraphQLNonNull)
        val oneOfInputType = assertNotNull(nonNullType.wrappedType as? GraphQLInputObjectType)
        assertEquals(
            expected = """
                input DeprecatedWrappedOneOfInput @oneOf {
                  blockquote: DeprecatedBlockQuoteInput
                  "the paragraph description"
                  paragraph: DeprecatedParagraphInput @deprecated(reason : "Deprecated")
                }
            """.trimIndent(),
            actual = SchemaPrinter().print(oneOfInputType).trim()
        )
    }

    @Test
    fun `oneOf sealed interface argument scalar type is valid`() {
        val kParameter = OneOfArgumentTestClass::oneOfScalarInput.findParameterByName("input")
        assertNotNull(kParameter)

        val result = generateArgument(generator, kParameter)

        assertEquals(expected = "input", actual = result.name)

        val nonNullType = assertNotNull(result.type as? GraphQLNonNull)
        val oneOfInputType = assertNotNull(nonNullType.wrappedType as? GraphQLInputObjectType)

        assertEquals(
            expected = """
                input UserByInput @oneOf {
                  criteria: UserByCriteriaInput
                  email: String
                  id: ID
                }
            """.trimIndent(),
            actual = SchemaPrinter().print(oneOfInputType).trim()
        )

        val criteriaInputType = oneOfInputType.getField("criteria")!!.type as GraphQLInputObjectType
        assertEquals(
            expected = """
                input UserByCriteriaInput {
                  address: String
                  name: String
                }
            """.trimIndent(),
            actual = SchemaPrinter().print(criteriaInputType).trim()
        )
    }

    @Test
    fun `oneOf unwrapped field uses custom GraphQL type annotation`() {
        val kParameter = OneOfArgumentTestClass::oneOfCustomGraphQLTypeInput.findParameterByName("input")
        assertNotNull(kParameter)

        val result = generateArgument(generator, kParameter)

        val nonNullType = assertNotNull(result.type as? GraphQLNonNull)
        val oneOfInputType = assertNotNull(nonNullType.wrappedType as? GraphQLInputObjectType)
        assertEquals(
            expected = """
                input UserByUnWrappedWithDirectivesInput @oneOf {
                  "something"
                  id: ID @deprecated(reason : "Deprecated") @simpleDirective
                }
            """.trimIndent(),
            actual = SchemaPrinter().print(oneOfInputType).trim()
        )
    }

    @Test
    fun `nested oneOf sealed interface argument object type is valid`() {
        val kParameter = OneOfArgumentTestClass::nestedOneOfInput.findParameterByName("input")
        assertNotNull(kParameter)
        val result = generateArgument(generator, kParameter)

        assertEquals(expected = "input", actual = result.name)
        val nonNullType = assertNotNull(result.type as? GraphQLNonNull)
        val oneOfInputType = assertNotNull(nonNullType.wrappedType as? GraphQLInputObjectType)

        assertEquals(
            expected = """
                input SearchTargetInput @oneOf {
                  organization: OrganizationSelectorInput
                  user: UserSelectorInput
                }
            """.trimIndent(),
            actual = SchemaPrinter().print(oneOfInputType).trim()
        )

        val organizationSelectorInputType = oneOfInputType.getField("organization")!!.type as GraphQLInputObjectType
        assertEquals(
            expected = """
                input OrganizationSelectorInput @oneOf {
                  id: ID
                  slug: String
                }
            """.trimIndent(),
            actual = SchemaPrinter().print(organizationSelectorInputType).trim()
        )

        val userSelectorInputType = oneOfInputType.getField("user")!!.type as GraphQLInputObjectType
        assertEquals(
            expected = """
                input UserSelectorInput @oneOf {
                  email: String
                  id: ID
                }
            """.trimIndent(),
            actual = SchemaPrinter().print(userSelectorInputType).trim()
        )
    }

    @Test
    fun `oneOf sealed class argument throws invalid target exception`() {
        val kParameter = OneOfArgumentTestClass::invalidOneOfInput.findParameterByName("input")
        assertNotNull(kParameter)
        assertFailsWith<InvalidGraphQLOneOfTargetException> {
            generateArgument(generator, kParameter)
        }
    }

    @Test
    fun `oneOf concrete data class argument throws invalid target exception`() {
        val kParameter = OneOfArgumentTestClass::concreteOneOfInput.findParameterByName("input")
        assertNotNull(kParameter)
        assertFailsWith<InvalidGraphQLOneOfTargetException> {
            generateArgument(generator, kParameter)
        }
    }

    @Test
    fun `oneOf non sealed interface argument throws invalid target exception`() {
        val kParameter = OneOfArgumentTestClass::nonSealedOneOfInput.findParameterByName("input")
        assertNotNull(kParameter)
        assertFailsWith<InvalidGraphQLOneOfTargetException> {
            generateArgument(generator, kParameter)
        }
    }

    @Test
    fun `oneOf empty sealed interface argument throws no implementation exception`() {
        val kParameter = OneOfArgumentTestClass::emptyOneOfInput.findParameterByName("input")
        assertNotNull(kParameter)
        assertFailsWith<NoGraphQLOneOfImplementationsException> {
            generateArgument(generator, kParameter)
        }
    }

    @Test
    fun `oneOf sealed interface argument throws missing field annotation exception`() {
        val kParameter = OneOfArgumentTestClass::missingFieldAnnotationOneOfInput.findParameterByName("input")
        assertNotNull(kParameter)
        assertFailsWith<MissingOneOfInputFieldAnnotationException> {
            generateArgument(generator, kParameter)
        }
    }

    @Test
    fun `oneOf sealed interface argument throws duplicate field name exception`() {
        val kParameter = OneOfArgumentTestClass::duplicateFieldNameOneOfInput.findParameterByName("input")
        assertNotNull(kParameter)
        assertFailsWith<DuplicateOneOfInputFieldException> {
            generateArgument(generator, kParameter)
        }
    }

    @Test
    fun `oneOf sealed interface argument throws invalid unwrapped field exception`() {
        val kParameter = OneOfArgumentTestClass::invalidUnwrappedFieldOneOfInput.findParameterByName("input")
        assertNotNull(kParameter)
        assertFailsWith<InvalidGraphQLOneOfUnwrappedFieldException> {
            generateArgument(generator, kParameter)
        }
    }

    @Test
    fun `oneOf input object includes custom directives`() {
        val kParameter = OneOfArgumentTestClass::directedOneOfInput.findParameterByName("input")
        assertNotNull(kParameter)

        val result = generateArgument(generator, kParameter)

        val nonNullType = assertNotNull(result.type as? GraphQLNonNull)
        val oneOfInputType = assertNotNull(nonNullType.wrappedType as? GraphQLInputObjectType)
        assertEquals(
            expected = setOf("oneOf", "simpleDirective"),
            actual = oneOfInputType.appliedDirectives.map { it.name }.toSet()
        )
    }

    @Test
    fun `oneOf unwrapped private constructor subtype is rejected during schema generation`() {
        val kParameter = OneOfArgumentTestClass::privateConstructorUnwrappedFieldOneOfInput.findParameterByName("input")
        assertNotNull(kParameter)

        assertFailsWith<InvalidGraphQLOneOfUnwrappedFieldException> {
            generateArgument(generator, kParameter)
        }
    }
}
