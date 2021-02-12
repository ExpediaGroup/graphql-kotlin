package com.expediagroup.graphql.plugin.client.generator.types

import com.expediagroup.graphql.plugin.client.generator.verifyGeneratedFileSpecContents
import org.junit.jupiter.api.Test

class GenerateGraphQLDocsIT {
    @Test
    fun `verify docs with format params do not blow up`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass
                import kotlinx.serialization.Serializable

                const val DOCS_QUERY: String = "query DocsQuery {\n  docQuery {\n    id\n  }\n}"

                @Serializable
                class DocsQuery : GraphQLClientRequest<DocsQuery.Result> {
                  override val query: String = DOCS_QUERY

                  override val operationName: String = "DocsQuery"

                  override fun responseType(): KClass<DocsQuery.Result> = DocsQuery.Result::class

                  /**
                   * Doc object with % and $ floating around
                   */
                  @Serializable
                  data class DocObject(
                    /**
                     * An id with a comment containing % and $ as well
                     */
                    val id: Int
                  )

                  @Serializable
                  data class Result(
                    /**
                     * Query to test doc strings
                     */
                    val docQuery: DocsQuery.DocObject
                  )
                }
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
