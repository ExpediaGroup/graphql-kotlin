/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
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
              data class UUID(
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
                scalarTypeToConverterMapping = mapOf("UUID" to ScalarConverterMapping("java.util.UUID", "com.expediagroup.graphql.plugin.generator.UUIDConverter"))
            )
        )
    }
}
