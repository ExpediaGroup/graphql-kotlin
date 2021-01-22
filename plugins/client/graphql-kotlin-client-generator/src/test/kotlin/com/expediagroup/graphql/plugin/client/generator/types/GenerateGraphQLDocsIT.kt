package com.expediagroup.graphql.plugin.client.generator.types

import com.expediagroup.graphql.plugin.client.generator.verifyGeneratedFileSpecContents
import org.junit.jupiter.api.Test

class GenerateGraphQLDocsIT {
    @Test
    fun `verify docs with format params do not blow up`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.execute
                import com.expediagroup.graphql.types.GraphQLResponse
                import kotlin.Int
                import kotlin.String

                const val TEST_QUERY: String = "query TestQuery {\n  docQuery {\n    id\n  }\n}"

                class TestQuery(
                  private val graphQLClient: GraphQLClient
                ) {
                  suspend fun execute(): GraphQLResponse<TestQuery.Result> = graphQLClient.execute(TEST_QUERY,
                      "TestQuery", null)

                  /**
                   * Doc object with % and $ floating around
                   */
                  data class DocObject(
                    /**
                     * An id with a comment containing % and $ as well
                     */
                    val id: Int
                  )

                  data class Result(
                    /**
                     * Query to test doc strings
                     */
                    val docQuery: TestQuery.DocObject
                  )
                }
            """.trimIndent()
        val query =
            """
                query TestQuery {
                  docQuery {
                    id
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(query, expected)
    }
}
