package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.CustomScalarConverterMapping
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorConfig
import com.expediagroup.graphql.plugin.generator.verifyGraphQLClientGeneration
import org.junit.jupiter.api.Test

class GenerateGraphQLCustomScalarTypeSpecIT {

    @Test
    fun `verify can generate custom scalar with converter mapping`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.client.GraphQLResult
            import com.expediagroup.graphql.plugin.generator.UUIDConverter
            import com.fasterxml.jackson.annotation.JsonCreator
            import com.fasterxml.jackson.annotation.JsonValue
            import kotlin.String
            import kotlin.jvm.JvmStatic

            const val CUSTOM_SCALAR_TEST_QUERY: String =
                "query CustomScalarTestQuery {\n  scalarQuery {\n    custom\n  }\n}"

            class CustomScalarTestQuery(
              private val graphQLClient: GraphQLClient
            ) {
              suspend fun customScalarTestQuery():
                  GraphQLResult<CustomScalarTestQuery.CustomScalarTestQueryResult> =
                  graphQLClient.executeOperation(CUSTOM_SCALAR_TEST_QUERY, "CustomScalarTestQuery", null)

              /**
               * Custom scalar representing UUID
               */
              class UUID(
                val value: java.util.UUID
              ) {
                @JsonValue
                fun rawValue() = converter.toJson(value)

                companion object {
                  val converter: UUIDConverter = UUIDConverter()

                  @JsonCreator
                  @JvmStatic
                  fun create(rawValue: String) = UUID(converter.toScalar(rawValue))
                }
              }

              /**
               * Wrapper that holds all supported scalar types
               */
              data class ScalarWrapper(
                /**
                 * Custom scalar
                 */
                val custom: CustomScalarTestQuery.UUID?
              )

              data class CustomScalarTestQueryResult(
                /**
                 * Query that returns wrapper object with all supported scalar types
                 */
                val scalarQuery: CustomScalarTestQuery.ScalarWrapper?
              )
            }
        """.trimIndent()

        val query = """
            query CustomScalarTestQuery {
              scalarQuery {
                custom
              }
            }
        """.trimIndent()

        verifyGraphQLClientGeneration(
            query,
            expected,
            GraphQLClientGeneratorConfig(
                packageName = "com.expediagroup.graphql.plugin.generator.integration",
                scalarTypeToConverterMapping = mapOf("UUID" to CustomScalarConverterMapping("java.util.UUID", "com.expediagroup.graphql.plugin.generator.UUIDConverter"))
            )
        )
    }
}
