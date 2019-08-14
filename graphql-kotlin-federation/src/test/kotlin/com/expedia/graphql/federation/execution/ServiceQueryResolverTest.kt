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
}"""

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
