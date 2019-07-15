package com.expedia.graphql.extensions

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.annotations.GraphQLName
import com.expedia.graphql.getTestSchemaConfigWithMockedDirectives
import com.expedia.graphql.testSchemaConfig
import com.expedia.graphql.toSchema
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
        val expected = """
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
        val expected = """
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
        fun queryById(@GraphQLID id: String) = TypeWithId(id, "junit")
    }

    class TypeWithId(
        @GraphQLID
        val id: String,
        val name: String
    )

    @Test
    fun `verify print result of a schema with GraphQL ID`() {
        val schema: GraphQLSchema = toSchema(queries = listOf(TopLevelObject(QueryWithId())), config = testSchemaConfig)

        val sdl = schema.print(includeDefaultSchemaDefinition = false, includeDirectives = false).trim()
        val expected = """
            type Query {
              queryById(id: String!): TypeWithId!
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
        val expected = """
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
        val expected = """
            #documented type
            type DocumentedType {
              #documented property
              id: Int!
            }

            type Query {
              #documented query
              documented(
                #documented argument
                id: Int!
              ): DocumentedType!
            }
        """.trimIndent()
        assertEquals(expected, sdl)
    }

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.FIELD_DEFINITION])
    internal annotation class CustomDirective

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
        val expected = """
            directive @customDirective on FIELD_DEFINITION

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

            #Directs the executor to include this field or fragment only when the `if` argument is true
            directive @include(if: Boolean!) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            #Directs the executor to skip this field or fragment when the `if`'argument is true.
            directive @skip(if: Boolean!) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            #Marks the target field/enum value as deprecated
            directive @deprecated(reason: String = "No longer supported") on FIELD_DEFINITION | ENUM_VALUE
        """.trimIndent()
        assertEquals(expected, sdl)
    }
}
