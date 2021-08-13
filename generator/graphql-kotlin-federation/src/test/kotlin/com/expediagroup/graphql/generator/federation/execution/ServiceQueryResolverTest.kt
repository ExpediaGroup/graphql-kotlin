/*
 * Copyright 2021 Expedia, Inc
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
import com.expediagroup.graphql.generator.federation.data.queries.federated.CustomScalar
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
interface Product @extends @key(fields : "id") {
  id: String! @external
  reviews: [Review!]!
}

type Book implements Product @extends @key(fields : "id") {
  author: User! @provides(fields : "name")
  id: String! @external
  reviews: [Review!]!
  shippingCost: String! @requires(fields : "weight")
  weight: Float! @external
}

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
type Query @extends {
  getSimpleNestedObject: [SelfReferenceObject]!
  hello(name: String!): String!
}

type SelfReferenceObject {
  description: String
  id: Int!
  nextObject: [SelfReferenceObject]!
}
"""

class ServiceQueryResolverTest {

    class CustomScalarFederatedHooks : FederatedSchemaGeneratorHooks(emptyList()) {
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
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.data.queries.federated"),
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
}
