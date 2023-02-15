/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.execution

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.data.queries.federated.v1.CustomScalar
import com.expediagroup.graphql.generator.federation.data.queries.simple.NestedQuery
import com.expediagroup.graphql.generator.federation.data.queries.simple.SimpleQuery
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

// SDL is returned without _entity and _service queries
const val FEDERATED_SERVICE_SDL =
"""
schema {
  query: Query
}

interface Product @extends @key(fields : "id") @key(fields : "upc") {
  id: String! @external
  reviews: [Review!]!
  upc: String! @external
}

type Author @extends @key(fields : "authorId") {
  authorId: Int! @external
  name: String! @external
}

type Book implements Product @extends @key(fields : "id") @key(fields : "upc") {
  author: User! @provides(fields : "name")
  id: String! @external
  reviews: [Review!]!
  shippingCost: String! @requires(fields : "weight")
  upc: String! @external
  weight: Float! @external
}

type Query @extends

type Review {
  body: String!
  content: String @deprecated(reason : "no longer supported, replace with use Review.body instead")
  customScalar: CustomScalar!
  id: String!
}

type User @extends @key(fields : "userId") {
  name: String! @external
  userId: Int! @external
}

""${'"'}
This is a multi-line comment on a custom scalar.
This should still work multiline and double quotes (") in the description.
Line 3.
""${'"'}
scalar CustomScalar"""

const val BASE_SERVICE_SDL =
"""
schema @link(import : ["@composeDirective", "@extends", "@external", "@inaccessible", "@interfaceObject", "@key", "@override", "@provides", "@requires", "@shareable", "@tag", "FieldSet"], url : "https://specs.apollo.dev/federation/v2.3"){
  query: Query
}

"Marks underlying custom directive to be included in the Supergraph schema"
directive @composeDirective(name: String!) repeatable on SCHEMA

"Marks target object as extending part of the federated schema"
directive @extends on OBJECT | INTERFACE

"Marks target field as external meaning it will be resolved by federated schema"
directive @external on OBJECT | FIELD_DEFINITION

"Marks location within schema as inaccessible from the GraphQL Gateway"
directive @inaccessible on SCALAR | OBJECT | FIELD_DEFINITION | ARGUMENT_DEFINITION | INTERFACE | UNION | ENUM | ENUM_VALUE | INPUT_OBJECT | INPUT_FIELD_DEFINITION

"Provides meta information to the router that this entity type is an interface in the supergraph."
directive @interfaceObject on OBJECT

"Space separated list of primary keys needed to access federated object"
directive @key(fields: FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE

"Links definitions within the document to external schemas."
directive @link(import: [String], url: String!) repeatable on SCHEMA

"Overrides fields resolution logic from other subgraph. Used for migrating fields from one subgraph to another."
directive @override(from: String!) on FIELD_DEFINITION

"Specifies the base type field set that will be selectable by the gateway"
directive @provides(fields: FieldSet!) on FIELD_DEFINITION

"Specifies required input field set from the base type for a resolver"
directive @requires(fields: FieldSet!) on FIELD_DEFINITION

"Indicates that given object and/or field can be resolved by multiple subgraphs"
directive @shareable repeatable on OBJECT | FIELD_DEFINITION

"Allows users to annotate fields and types with additional metadata information"
directive @tag(name: String!) repeatable on SCALAR | OBJECT | FIELD_DEFINITION | ARGUMENT_DEFINITION | INTERFACE | UNION | ENUM | ENUM_VALUE | INPUT_OBJECT | INPUT_FIELD_DEFINITION

type Query {
  _service: _Service!
  getSimpleNestedObject: [SelfReferenceObject]!
  hello(name: String!): String!
}

type SelfReferenceObject {
  description: String
  id: Int!
  nextObject: [SelfReferenceObject]!
}

type _Service {
  sdl: String!
}

"Federation type representing set of fields"
scalar FieldSet
"""

const val FEDERATED_SERVICE_SDL_V2 =
"""
schema @link(import : ["@composeDirective", "@extends", "@external", "@inaccessible", "@interfaceObject", "@key", "@override", "@provides", "@requires", "@shareable", "@tag", "FieldSet"], url : "https://specs.apollo.dev/federation/v2.3"){
  query: Query
}

"Marks underlying custom directive to be included in the Supergraph schema"
directive @composeDirective(name: String!) repeatable on SCHEMA

directive @custom on SCHEMA | SCALAR | OBJECT | FIELD_DEFINITION | ARGUMENT_DEFINITION | INTERFACE | UNION | ENUM | ENUM_VALUE | INPUT_OBJECT | INPUT_FIELD_DEFINITION

"Marks target object as extending part of the federated schema"
directive @extends on OBJECT | INTERFACE

"Marks target field as external meaning it will be resolved by federated schema"
directive @external on OBJECT | FIELD_DEFINITION

"Marks location within schema as inaccessible from the GraphQL Gateway"
directive @inaccessible on SCALAR | OBJECT | FIELD_DEFINITION | ARGUMENT_DEFINITION | INTERFACE | UNION | ENUM | ENUM_VALUE | INPUT_OBJECT | INPUT_FIELD_DEFINITION

"Provides meta information to the router that this entity type is an interface in the supergraph."
directive @interfaceObject on OBJECT

"Space separated list of primary keys needed to access federated object"
directive @key(fields: FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE

"Links definitions within the document to external schemas."
directive @link(import: [String], url: String!) repeatable on SCHEMA

"Overrides fields resolution logic from other subgraph. Used for migrating fields from one subgraph to another."
directive @override(from: String!) on FIELD_DEFINITION

"Specifies the base type field set that will be selectable by the gateway"
directive @provides(fields: FieldSet!) on FIELD_DEFINITION

"Specifies required input field set from the base type for a resolver"
directive @requires(fields: FieldSet!) on FIELD_DEFINITION

"Indicates that given object and/or field can be resolved by multiple subgraphs"
directive @shareable repeatable on OBJECT | FIELD_DEFINITION

"Allows users to annotate fields and types with additional metadata information"
directive @tag(name: String!) repeatable on SCALAR | OBJECT | FIELD_DEFINITION | ARGUMENT_DEFINITION | INTERFACE | UNION | ENUM | ENUM_VALUE | INPUT_OBJECT | INPUT_FIELD_DEFINITION

interface Product @key(fields : "id", resolvable : true) @key(fields : "upc", resolvable : true) {
  id: String!
  reviews: [Review!]!
  upc: String!
}

union _Entity = Author | Book | User

type Author @key(fields : "authorId", resolvable : true) {
  authorId: Int!
  name: String!
}

type Book implements Product @key(fields : "id", resolvable : true) @key(fields : "upc", resolvable : true) {
  author: User! @provides(fields : "name")
  id: String!
  reviews: [Review!]!
  shippingCost: String! @requires(fields : "weight")
  upc: String!
  weight: Float! @external
}

type CustomScalar {
  value: String!
}

type Query {
  "Union of all types that use the @key directive, including both types native to the schema and extended types"
  _entities(representations: [_Any!]!): [_Entity]!
  _service: _Service!
}

type Review {
  body: String! @custom
  content: String @deprecated(reason : "no longer supported, replace with use Review.body instead")
  customScalar: CustomScalar!
  id: String!
}

type User @key(fields : "userId", resolvable : true) {
  name: String!
  userId: Int!
}

type _Service {
  sdl: String!
}

"Federation type representing set of fields"
scalar FieldSet

"Federation scalar type used to represent any external entities passed to _entities query."
scalar _Any
"""

class ServiceQueryResolverTest {

    class CustomScalarFederatedHooks : FederatedSchemaGeneratorHooks(emptyList(), optInFederationV2 = false) {
        override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
            CustomScalar::class -> graphqlCustomScalar
            else -> super.willGenerateGraphQLType(type)
        }

        private val graphqlCustomScalar = GraphQLScalarType.newScalar()
            .name("CustomScalar")
            .description(
                """
                    This is a multi-line comment on a custom scalar.
                    This should still work multiline and double quotes (") in the description.
                    Line 3.
                """.trimIndent()
            )
            .coercing(CustomScalarCoercing()).build()

        private class CustomScalarCoercing : Coercing<CustomScalar, String> {
            override fun parseValue(input: Any): CustomScalar = CustomScalar(serialize(input))

            override fun parseLiteral(input: Any): CustomScalar {
                val customValue = (input as? StringValue)?.value ?: throw CoercingParseValueException("Cannot parse $input to CustomScalar")
                return CustomScalar(customValue)
            }

            override fun serialize(dataFetcherResult: Any): String = dataFetcherResult.toString()
        }
    }

    @Test
    fun `verify can retrieve SDL using _service query`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.data.queries.federated.v1"),
            hooks = CustomScalarFederatedHooks()
        )

        val schema = toFederatedSchema(config = config)
        val query =
            """
                query sdlQuery {
                  _service {
                    sdl
                  }
                }
            """.trimIndent()
        val executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .build()
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.executeAsync(executionInput).get().toSpecification()

        assertNotNull(result["data"] as? Map<*, *>) { data ->
            assertNotNull(data["_service"] as? Map<*, *>) { queryResult ->
                val sdl = queryResult["sdl"] as? String
                assertEquals(FEDERATED_SERVICE_SDL.trim(), sdl)
            }
        }
    }

    @Test
    fun `verify can retrieve SDL using _service query for non-federated schemas`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.data.queries.simple"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        val schema = toFederatedSchema(config = config, queries = listOf(TopLevelObject(SimpleQuery()), TopLevelObject(NestedQuery())))
        val query =
            """
                query sdlQuery {
                  _service {
                    sdl
                  }
                }
            """.trimIndent()
        val executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .build()
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.executeAsync(executionInput).get().toSpecification()

        assertNotNull(result["data"] as? Map<*, *>) { data ->
            assertNotNull(data["_service"] as? Map<*, *>) { queryResult ->
                val sdl = queryResult["sdl"] as? String
                assertEquals(BASE_SERVICE_SDL.trim(), sdl)
            }
        }
    }

    @Test
    fun `verify can retrieve Federation v2 SDL using _service query`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.data.queries.federated.v2"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        val schema = toFederatedSchema(config = config)
        val query =
            """
                query sdlQuery {
                  _service {
                    sdl
                  }
                }
            """.trimIndent()
        val executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .build()
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.executeAsync(executionInput).get().toSpecification()

        assertNotNull(result["data"] as? Map<*, *>) { data ->
            assertNotNull(data["_service"] as? Map<*, *>) { queryResult ->
                val sdl = queryResult["sdl"] as? String
                assertEquals(FEDERATED_SERVICE_SDL_V2.trim(), sdl)
            }
        }
    }
}
