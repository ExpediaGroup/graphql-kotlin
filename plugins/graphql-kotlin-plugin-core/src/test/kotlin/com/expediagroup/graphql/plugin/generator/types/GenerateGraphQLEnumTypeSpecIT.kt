package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.verifyGraphQLClientGeneration
import org.junit.jupiter.api.Test

class GenerateGraphQLEnumTypeSpecIT {

    @Test
    fun `verify enum types are correctly generated`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.client.GraphQLResult
            import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
            import kotlin.Deprecated
            import kotlin.String

            const val ENUM_TEST_QUERY: String = "query EnumTestQuery {\n  enumQuery\n}"

            class EnumTestQuery(
              private val graphQLClient: GraphQLClient
            ) {
              suspend fun enumTestQuery(): GraphQLResult<EnumTestQuery.EnumTestQueryResult> =
                  graphQLClient.executeOperation(ENUM_TEST_QUERY, "EnumTestQuery", null)

              /**
               * Custom enum description
               */
              enum class CustomEnum {
                /**
                 * First enum value
                 */
                ONE,

                /**
                 * Second enum value
                 */
                TWO,

                /**
                 * Third enum value
                 */
                @Deprecated(message = "only goes up to two")
                THREE,

                /**
                 * This is a default enum value that will be used when attempting to deserialize unknown value.
                 */
                @JsonEnumDefaultValue
                __UNKNOWN_VALUE
              }

              data class EnumTestQueryResult(
                /**
                 * Query that returns enum value
                 */
                val enumQuery: EnumTestQuery.CustomEnum?
              )
            }
        """.trimIndent()

        val query = """
            query EnumTestQuery {
              enumQuery
            }
        """.trimIndent()

        verifyGraphQLClientGeneration(query, expected)
    }
}
