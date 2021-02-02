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
                import com.expediagroup.graphql.client.GraphQLClientRequest
                import com.expediagroup.graphql.types.GraphQLResponse
                import java.lang.Class
                import kotlin.Int
                import kotlin.String

                const val DOCS_QUERY: String = "query DocsQuery {\n  docQuery {\n    id\n  }\n}"

                class DocsQuery : GraphQLClientRequest(DOCS_QUERY, "DocsQuery") {
                  override fun responseType(): Class<DocsQuery.Result> = DocsQuery.Result::class.java

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
                    val docQuery: DocsQuery.DocObject
                  )
                }

                suspend fun GraphQLClient<*>.executeDocsQuery(request: DocsQuery): GraphQLResponse<DocsQuery.Result>
                    = execute(request)
            """.trimIndent()
        val query =
            """
                query DocsQuery {
                  docQuery {
                    id
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(query, expected)
    }
}
