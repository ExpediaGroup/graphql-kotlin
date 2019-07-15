package com.expedia.graphql.federation

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.extensions.print
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import test.data.queries.federated.Book
import test.data.queries.federated.User
import test.data.queries.simple.SimpleQuery

class FederatedSchemaGeneratorTest {

    @Suppress("LongMethod")
    @Test
    fun `verify can generate federated schema`() {
        val expectedFederatedSchema = """
            schema {
              query: Query
            }

            #Marks target field as external meaning it will be resolved by federated schema
            directive @external on FIELD_DEFINITION

            #Marks target object as part of the federated schema
            directive @extends on OBJECT | INTERFACE

            #Specifies the base type field set that will be selectable by the gateway
            directive @provides(fields: _FieldSet!) on FIELD_DEFINITION

            #Space separated list of primary keys needed to access federated object
            directive @key(fields: _FieldSet!) on OBJECT | INTERFACE

            #Specifies required input field set from the base type for a resolver
            directive @requires(fields: _FieldSet!) on FIELD_DEFINITION

            interface Product @extends @key(fields : "id") {
              id: String! @external
              reviews: [Review!]!
            }

            union _Entity = Book | User

            type Book implements Product @extends @key(fields : "id") {
              author: User! @provides(fields : "name")
              id: String! @external
              reviews: [Review!]!
              shippingCost: String! @requires(fields : "weight")
              weight: Float! @external
            }

            type Query {
              #Union of all types that use the @key directive, including both types native to the schema and extended types
              _entities(representations: [_Any!]!): [_Entity]!
              _service: _Service
            }

            type Review {
              body: String!
              id: String!
            }

            type User @extends @key(fields : "userId") {
              name: String! @external
              userId: Int! @external
            }

            type _Service {
              sdl: String!
            }

            #Federation scalar type used to represent any external entities passed to _entities query.
            scalar _Any

            #Federation type representing set of fields
            scalar _FieldSet

            #Directs the executor to include this field or fragment only when the `if` argument is true
            directive @include(if: Boolean!) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            #Directs the executor to skip this field or fragment when the `if`'argument is true.
            directive @skip(if: Boolean!) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            #Marks the target field/enum value as deprecated
            directive @deprecated(reason: String = "No longer supported") on FIELD_DEFINITION | ENUM_VALUE
        """.trimIndent()
        val bookResolver = object : FederatedTypeResolver<Book> {
            override fun resolve(keys: Map<String, Any>): Book {
                val book = Book(keys["id"].toString())
                keys["weight"]?.toString()?.toDoubleOrNull()?.let {
                    book.weight = it
                }
                return book
            }
        }
        val userResolver = object : FederatedTypeResolver<User> {
            override fun resolve(keys: Map<String, Any>): User {
                val id = keys["userId"].toString().toInt()
                val name = keys["name"].toString()
                return User(id, name)
            }
        }

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("test.data.queries.federated"),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry(mapOf("Book" to bookResolver, "User" to userResolver)))
        )

        val schema = toFederatedSchema(config)
        assertEquals(expectedFederatedSchema, schema.print().trim())
    }

    @Test
    fun `verify generator does not add federation queries for non-federated schemas`() {
        val expectedSchema = """
            schema {
              query: Query
            }

            type Query {
              _service: _Service
              hello(name: String!): String!
            }

            type _Service {
              sdl: String!
            }
        """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("test.data.queries.simple"),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry(emptyMap()))
        )

        val schema = toFederatedSchema(config, listOf(TopLevelObject(SimpleQuery())))
        assertEquals(expectedSchema, schema.print(includeDirectives = false).trim())
    }
}
