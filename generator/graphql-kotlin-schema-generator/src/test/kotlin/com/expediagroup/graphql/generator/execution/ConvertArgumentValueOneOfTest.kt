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
package com.expediagroup.graphql.generator.execution

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.annotations.GraphQLOneOf
import com.expediagroup.graphql.generator.annotations.GraphQLOneOfField
import com.expediagroup.graphql.generator.annotations.GraphQLOneOfFieldType
import com.expediagroup.graphql.generator.exceptions.InvalidGraphQLOneOfUnwrappedFieldException
import com.expediagroup.graphql.generator.scalars.ID
import org.junit.jupiter.api.Test
import kotlin.reflect.full.findParameterByName
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ConvertArgumentValueOneOfTest {

    class TestFunctions {
        fun oneOfInput(input: PostElementInput): String = TODO()
        fun oneOfScalarInput(input: UserByInput): String = TODO()
        fun nestedOneOfInput(input: SearchTargetInput): String = TODO()
        fun listOneOfInput(input: List<PostElementInput>): String = TODO()
        fun invalidUnwrappedOneOfInput(input: InvalidUnwrappedOneOfInput): String = TODO()
        fun renamedUnwrappedOneOfInput(input: RenamedUserByInput): String = TODO()
    }

    @GraphQLOneOf
    sealed interface PostElementInput {
        @GraphQLOneOfField("paragraph")
        data class ParagraphInput(val text: String) : PostElementInput
        @GraphQLOneOfField("blockquote")
        data class BlockQuoteInput(val value: String, val attribution: String?, val attributionUrl: String?) : PostElementInput
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
    sealed interface InvalidUnwrappedOneOfInput {
        @GraphQLOneOfField("invalid", GraphQLOneOfFieldType.UNWRAPPED)
        data class Invalid(val first: String, val second: String) : InvalidUnwrappedOneOfInput
    }

    @GraphQLOneOf
    sealed interface RenamedUserByInput {
        @GraphQLOneOfField("id", GraphQLOneOfFieldType.UNWRAPPED)
        data class UserByID(@param:GraphQLName("value") val id: ID) : RenamedUserByInput
    }

    @Test
    fun `oneOf sealed input object is parsed into subtype`() {
        val kParam = assertNotNull(TestFunctions::oneOfInput.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to mapOf(
                    "paragraph" to mapOf(
                        "text" to "test"
                    )
                )
            )
        )
        val castResult = assertIs<PostElementInput.ParagraphInput>(result)
        assertEquals("test", castResult.text)
    }

    @Test
    fun `oneOf sealed scalar input is parsed into subtype`() {
        val kParam = assertNotNull(TestFunctions::oneOfScalarInput.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to mapOf(
                    "id" to "1234"
                )
            )
        )

        val castResult = assertIs<UserByInput.UserByID>(result)
        assertEquals("1234", castResult.id.value)
    }

    @Test
    fun `oneOf sealed scalar string input is parsed into subtype`() {
        val kParam = assertNotNull(TestFunctions::oneOfScalarInput.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to mapOf(
                    "email" to "sam@example.com"
                )
            )
        )

        val castResult = assertIs<UserByInput.UserByEmail>(result)
        assertEquals("sam@example.com", castResult.email)
    }

    @Test
    fun `oneOf sealed wrapped object input is parsed into subtype`() {
        val kParam = assertNotNull(TestFunctions::oneOfScalarInput.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to mapOf(
                    "criteria" to mapOf(
                        "name" to "Sam",
                        "address" to "Seattle"
                    )
                )
            )
        )

        val castResult = assertIs<UserByInput.UserByCriteria>(result)
        assertEquals("Sam", castResult.name)
        assertEquals("Seattle", castResult.address)
    }

    @Test
    fun `nested oneOf sealed input object is parsed into subtype`() {
        val kParam = assertNotNull(TestFunctions::nestedOneOfInput.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to mapOf(
                    "user" to mapOf(
                        "email" to "sam@example.com"
                    )
                )
            )
        )

        val castResult = assertIs<SearchTargetInput.User>(result)
        val userSelector = assertIs<UserSelectorInput.Email>(castResult.value)
        assertEquals("sam@example.com", userSelector.value)
    }

    @Test
    fun `list oneOf sealed input object is parsed into subtypes`() {
        val kParam = assertNotNull(TestFunctions::listOneOfInput.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to listOf(
                    mapOf(
                        "paragraph" to mapOf(
                            "text" to "test"
                        )
                    ),
                    mapOf(
                        "blockquote" to mapOf(
                            "value" to "quote"
                        )
                    )
                )
            )
        )

        val listResult = assertIs<List<*>>(result)
        val paragraph = assertIs<PostElementInput.ParagraphInput>(listResult[0])
        assertEquals("test", paragraph.text)
        val blockquote = assertIs<PostElementInput.BlockQuoteInput>(listResult[1])
        assertEquals("quote", blockquote.value)
        assertEquals(null, blockquote.attribution)
        assertEquals(null, blockquote.attributionUrl)
    }

    @Test
    fun `oneOf unwrapped subtype with multiple constructor parameters throws`() {
        val kParam = assertNotNull(TestFunctions::invalidUnwrappedOneOfInput.findParameterByName("input"))
        assertFailsWith<InvalidGraphQLOneOfUnwrappedFieldException> {
            convertArgumentValue(
                "input",
                kParam,
                mapOf(
                    "input" to mapOf(
                        "invalid" to "value"
                    )
                )
            )
        }
    }

    @Test
    fun `oneOf unwrapped subtype with renamed constructor parameter is parsed into subtype`() {
        val kParam = assertNotNull(TestFunctions::renamedUnwrappedOneOfInput.findParameterByName("input"))
        val result = convertArgumentValue(
            "input",
            kParam,
            mapOf(
                "input" to mapOf(
                    "id" to "1234"
                )
            )
        )
        val castResult = assertIs<RenamedUserByInput.UserByID>(result)
        assertEquals("1234", castResult.id.value)
    }
}
