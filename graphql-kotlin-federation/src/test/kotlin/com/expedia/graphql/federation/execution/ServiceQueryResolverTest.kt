package com.expedia.graphql.federation.execution

import com.expedia.graphql.federation.FederatedSchemaGeneratorConfig
import com.expedia.graphql.federation.FederatedSchemaGeneratorHooks
import com.expedia.graphql.federation.FederatedTypeRegistry
import com.expedia.graphql.federation.toFederatedSchema
import graphql.ExecutionInput
import graphql.GraphQL
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

// SDL is returned without _entity and _service queries
const val FEDERATED_SERVICE_SDL = """schema {
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

type Book implements Product @extends @key(fields : "id") {
  author: User! @provides(fields : "name")
  id: String! @external
  reviews: [Review!]!
  shippingCost: String! @requires(fields : "weight")
  weight: Float! @external
}

type Query {
}

type Review {
  body: String!
  id: String!
}

type User @extends @key(fields : "userId") {
  name: String! @external
  userId: Int! @external
}

#Federation type representing set of fields
scalar _FieldSet

#Directs the executor to include this field or fragment only when the `if` argument is true
directive @include(if: Boolean!) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

#Directs the executor to skip this field or fragment when the `if`'argument is true.
directive @skip(if: Boolean!) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

#Marks the target field/enum value as deprecated
directive @deprecated(reason: String = "No longer supported") on FIELD_DEFINITION | ENUM_VALUE"""

class ServiceQueryResolverTest {

    @Test
    fun `verify can retrieve SDL using _service query`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("test.data.queries.federated"),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry(emptyMap()))
        )

        val schema = toFederatedSchema(config)
        val query = """
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
        val result = graphQL.executeAsync(executionInput).get()

        val data = result.toSpecification()["data"] as? Map<*, *>
        assertNotNull(data)
        val queryResult = data["_service"] as? Map<*, *>
        assertNotNull(queryResult)
        val sdl = queryResult["sdl"] as? String
        assertEquals(FEDERATED_SERVICE_SDL, sdl)
    }
}
