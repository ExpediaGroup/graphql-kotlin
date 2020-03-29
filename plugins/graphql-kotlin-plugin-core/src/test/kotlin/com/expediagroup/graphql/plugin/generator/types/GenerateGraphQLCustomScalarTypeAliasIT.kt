package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.verifyGraphQLClientGeneration
import org.junit.jupiter.api.Test

class GenerateGraphQLCustomScalarTypeAliasIT {

    @Test
    fun `verify can generate type aliases for GraphQL ID and custom scalars`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.client.GraphQLResult
            import kotlin.String

            /**
             * Custom scalar representing UUID
             */
            typealias UUID = String

            const val SCALAR_ALIAS_TEST_QUERY: String =
                "query ScalarAliasTestQuery {\n  scalarQuery {\n    custom\n  }\n}"

            class ScalarAliasTestQuery(
              private val graphQLClient: GraphQLClient
            ) {
              suspend fun scalarAliasTestQuery(): GraphQLResult<ScalarAliasTestQuery.ScalarAliasTestQueryResult>
                  = graphQLClient.executeOperation(SCALAR_ALIAS_TEST_QUERY, "ScalarAliasTestQuery", null)

              /**
               * Wrapper that holds all supported scalar types
               */
              data class ScalarWrapper(
                /**
                 * Custom scalar
                 */
                val custom: UUID?
              )

              data class ScalarAliasTestQueryResult(
                /**
                 * Query that returns wrapper object with all supported scalar types
                 */
                val scalarQuery: ScalarAliasTestQuery.ScalarWrapper?
              )
            }
        """.trimIndent()

        val query = """
            query ScalarAliasTestQuery {
              scalarQuery {
                custom
              }
            }
        """.trimIndent()
        verifyGraphQLClientGeneration(query, expected)
    }
}
