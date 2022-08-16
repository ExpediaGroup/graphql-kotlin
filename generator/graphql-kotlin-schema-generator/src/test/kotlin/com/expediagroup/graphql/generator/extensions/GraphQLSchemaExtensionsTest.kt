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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.getTestSchemaConfigWithMockedDirectives
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import graphql.introspection.Introspection
import graphql.schema.GraphQLSchema
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

@Suppress("Detekt.TrailingWhitespace", "Detekt.FunctionOnlyReturningConstant", "Detekt.UseDataClass", "Detekt.UnusedPrivateMember")
class GraphQLSchemaExtensionsTest {

    class SimpleQuery {
        fun basic(msg: String) = msg

        fun nullable(msg: String?) = msg
    }

    @Test
    fun `verify print result of a simple schema`() {
        val schema: GraphQLSchema = toSchema(queries = listOf(TopLevelObject(SimpleQuery())), config = testSchemaConfig)

        val sdl = schema.print(includeDirectives = false).trim()
        val expected =
            """
                schema {
                  query: Query
                }

                type Query {
                  basic(msg: String!): String!
                  nullable(msg: String): String
                }
            """.trimIndent()
        assertEquals(expected, sdl)
    }

    @Test
    fun `verify print result of a simple schema with no scalars`() {
        val schema: GraphQLSchema = toSchema(queries = listOf(TopLevelObject(SimpleQuery())), config = testSchemaConfig)

        val sdl = schema.print(includeDirectives = false, includeScalarTypes = false).trim()
        val expected =
            """
                schema {
                  query: Query
                }

                type Query {
                  basic(msg: String!): String!
                  nullable(msg: String): String
                }
            """.trimIndent()
        assertEquals(expected, sdl)
    }

    class RenamedQuery {
        @GraphQLName("renamedFunction")
        fun original(id: Int) = OriginalType(id)
    }

    @GraphQLName("RenamedType")
    class OriginalType(
        @GraphQLName("renamedProperty")
        val originalProperty: Int
    )

    @Test
    fun `verify print result of a schema with renamed fields`() {
        val schema: GraphQLSchema = toSchema(queries = listOf(TopLevelObject(RenamedQuery())), config = testSchemaConfig)

        val sdl = schema.print(includeDefaultSchemaDefinition = false, includeDirectives = false).trim()
        val expected =
            """
                type Query {
                  renamedFunction(id: Int!): RenamedType!
                }

                type RenamedType {
                  renamedProperty: Int!
                }
            """.trimIndent()
        assertEquals(expected, sdl)
    }

    class QueryWithId {
        fun queryById(id: ID) = TypeWithId(id, "junit")
    }

    class TypeWithId(
        val id: ID,
        val name: String
    )

    @Test
    fun `verify print result of a schema with GraphQL ID`() {
        val schema: GraphQLSchema = toSchema(queries = listOf(TopLevelObject(QueryWithId())), config = testSchemaConfig)

        val sdl = schema.print(includeDefaultSchemaDefinition = false, includeDirectives = false).trim()
        val expected =
            """
                type Query {
                  queryById(id: ID!): TypeWithId!
                }

                type TypeWithId {
                  id: ID!
                  name: String!
                }
            """.trimIndent()
        assertEquals(expected, sdl)
    }

    class QueryWithExcludedFields {
        @GraphQLIgnore
        fun notPartOfTheSchema() = "ignore me!"

        private fun privateFunction() = "ignored private function"

        fun visible(id: Int) = TypeWithExcludedFields(id, UUID.randomUUID().toString(), UUID.randomUUID().toString())
    }

    class TypeWithExcludedFields(
        val id: Int,
        @GraphQLIgnore
        val hash: String,
        private val secret: String
    )

    @Test
    fun `verify print result of a schema with ignored elements`() {
        val schema: GraphQLSchema = toSchema(queries = listOf(TopLevelObject(QueryWithExcludedFields())), config = testSchemaConfig)

        val sdl = schema.print(includeDefaultSchemaDefinition = false, includeDirectives = false).trim()
        val expected =
            """
                type Query {
                  visible(id: Int!): TypeWithExcludedFields!
                }

                type TypeWithExcludedFields {
                  id: Int!
                }
            """.trimIndent()
        assertEquals(expected, sdl)
    }

    class DocumentedQuery {
        @GraphQLDescription("documented query")
        fun documented(@GraphQLDescription("documented argument") id: Int) = DocumentedType(id)

        @GraphQLDescription("escaped pattern: `^\\+[1-9]\\d{7,14}$`")
        fun documentedWithEscapeCharacters() = "escaped \\"

        @GraphQLDescription("""raw pattern: `^\+[1-9]\d{7,14}$`""")
        fun documentedWithRawEscapeCharacters() =
            """escaped raw \"""
    }

    @GraphQLDescription("documented type")
    class DocumentedType(
        @GraphQLDescription("documented property")
        val id: Int
    )

    @Test
    fun `verify print result of a documented schema`() {
        val schema: GraphQLSchema = toSchema(queries = listOf(TopLevelObject(DocumentedQuery())), config = testSchemaConfig)

        val sdl = schema.print(includeDefaultSchemaDefinition = false, includeDirectives = false).trim()
        val expected =
            """
                "documented type"
                type DocumentedType {
                  "documented property"
                  id: Int!
                }

                type Query {
                  "documented query"
                  documented(
                    "documented argument"
                    id: Int!
                  ): DocumentedType!
                  "escaped pattern: `^\\+[1-9]\\d{7,14}${'$'}`"
                  documentedWithEscapeCharacters: String!
                  "raw pattern: `^\\+[1-9]\\d{7,14}${'$'}`"
                  documentedWithRawEscapeCharacters: String!
                }
            """.trimIndent()
        assertEquals(expected, sdl)
    }

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.FIELD_DEFINITION])
    annotation class CustomDirective

    class QueryWithDirectives {
        fun echo(msg: String): String = msg

        @CustomDirective
        fun withDirective() = "with directive"

        @CustomDirective
        @Deprecated(message = "deprecated with directive")
        fun deprecatedWithDirective() = "deprecated with directive"

        @Deprecated(message = "unsupported message")
        fun deprecatedEcho(msg: String): String = msg

        @Deprecated(message = "unsupported message", replaceWith = ReplaceWith(expression = "echo()"))
        fun deprecatedEchoWithReplacement(msg: String): String = msg

        fun simpleEnum(): SimpleEnum = SimpleEnum.ONE

        fun classWithDirective(msg: String): ClassWithDirective = ClassWithDirective(msg)
    }

    class ClassWithDirective(@property:CustomDirective val msg: String)

    enum class SimpleEnum {
        ONE,
        @Deprecated("deprecated enum value")
        TWO,
        @Deprecated("deprecated enum value", replaceWith = ReplaceWith("ONE"))
        THREE
    }

    @Test
    fun `verify print result of a schema with directives`() {
        val schema: GraphQLSchema = toSchema(queries = listOf(TopLevelObject(QueryWithDirectives())), config = getTestSchemaConfigWithMockedDirectives())

        val sdl = schema.print(includeDefaultSchemaDefinition = false).trim()
        val expected =
            """
                directive @customDirective on FIELD_DEFINITION

                "Marks the field, argument, input field or enum value as deprecated"
                directive @deprecated(
                    "The reason for the deprecation"
                    reason: String = "No longer supported"
                  ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

                "Directs the executor to include this field or fragment only when the `if` argument is true"
                directive @include(
                    "Included when true."
                    if: Boolean!
                  ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

                "Directs the executor to skip this field or fragment when the `if` argument is true."
                directive @skip(
                    "Skipped when true."
                    if: Boolean!
                  ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

                "Exposes a URL that specifies the behaviour of this scalar."
                directive @specifiedBy(
                    "The URL that specifies the behaviour of this scalar."
                    url: String!
                  ) on SCALAR

                type ClassWithDirective {
                  msg: String! @customDirective
                }

                type Query {
                  classWithDirective(msg: String!): ClassWithDirective!
                  deprecatedEcho(msg: String!): String! @deprecated(reason : "unsupported message")
                  deprecatedEchoWithReplacement(msg: String!): String! @deprecated(reason : "unsupported message, replace with echo()")
                  deprecatedWithDirective: String! @customDirective @deprecated(reason : "deprecated with directive")
                  echo(msg: String!): String!
                  simpleEnum: SimpleEnum!
                  withDirective: String! @customDirective
                }

                enum SimpleEnum {
                  ONE
                  THREE @deprecated(reason : "deprecated enum value, replace with ONE")
                  TWO @deprecated(reason : "deprecated enum value")
                }
            """.trimIndent()
        assertEquals(expected, sdl)
    }
}
