package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.verifyGraphQLClientGeneration
import org.junit.jupiter.api.Test

class GenerateGraphQLInputObjectTypeSpecIT {

    @Test
    fun `verify we can generate valid input object type spec`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.client.GraphQLResult
            import kotlin.Boolean
            import kotlin.String

            const val INPUT_OBJECT_TEST_QUERY: String =
                "query InputObjectTestQuery {\n  inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n}"

            class InputObjectTestQuery(
              private val graphQLClient: GraphQLClient
            ) {
              suspend fun inputObjectTestQuery(): GraphQLResult<InputObjectTestQuery.InputObjectTestQueryResult>
                  = graphQLClient.executeOperation(INPUT_OBJECT_TEST_QUERY, "InputObjectTestQuery", null)

              data class InputObjectTestQueryResult(
                /**
                 * Query that accepts some input arguments
                 */
                val inputObjectQuery: Boolean?
              )
            }
        """.trimIndent()

        val query = """
            query InputObjectTestQuery {
              inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )
            }
        """.trimIndent()
        verifyGraphQLClientGeneration(query, expected)
    }

    @Test
    fun `verify we can generate objects using aliases`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.client.GraphQLResult
            import kotlin.Boolean
            import kotlin.String

            const val ALIAS_TEST_QUERY: String =
                "query AliasTestQuery {\n  first: inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n  second: inputObjectQuery(criteria: { min: 5.0, max: 10.0 } )\n}"

            class AliasTestQuery(
              private val graphQLClient: GraphQLClient
            ) {
              suspend fun aliasTestQuery(): GraphQLResult<AliasTestQuery.AliasTestQueryResult> =
                  graphQLClient.executeOperation(ALIAS_TEST_QUERY, "AliasTestQuery", null)

              data class AliasTestQueryResult(
                /**
                 * Query that accepts some input arguments
                 */
                val first: Boolean?,
                /**
                 * Query that accepts some input arguments
                 */
                val second: Boolean?
              )
            }
        """.trimIndent()

        val query = """
            query AliasTestQuery {
              first: inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )
              second: inputObjectQuery(criteria: { min: 5.0, max: 10.0 } )
            }
        """.trimIndent()
        verifyGraphQLClientGeneration(query, expected)
    }
}
