/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.plugin.client.generator.types

import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorConfig
import com.expediagroup.graphql.plugin.client.generator.GraphQLScalar
import com.expediagroup.graphql.plugin.client.generator.verifyGeneratedFileSpecContents
import org.junit.jupiter.api.Test

class GenerateGraphQLCustomScalarTypeSpecIT {

    @Test
    fun `verify can generate custom scalar with converter mapping`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.execute
                import com.expediagroup.graphql.plugin.generator.UUIDScalarConverter
                import com.expediagroup.graphql.types.GraphQLResponse
                import com.fasterxml.jackson.annotation.JsonCreator
                import com.fasterxml.jackson.annotation.JsonValue
                import kotlin.Any
                import kotlin.String
                import kotlin.jvm.JvmStatic

                const val CUSTOM_SCALAR_TEST_QUERY: String =
                    "query CustomScalarTestQuery {\n  scalarQuery {\n    custom\n  }\n}"

                class CustomScalarTestQuery(
                  private val graphQLClient: GraphQLClient
                ) {
                  suspend fun execute(): GraphQLResponse<CustomScalarTestQuery.Result> =
                      graphQLClient.execute(CUSTOM_SCALAR_TEST_QUERY, "CustomScalarTestQuery", null)

                  /**
                   * Custom scalar representing UUID
                   */
                  data class UUID(
                    val value: java.util.UUID
                  ) {
                    @JsonValue
                    fun rawValue() = converter.toJson(value)

                    companion object {
                      val converter: UUIDScalarConverter = UUIDScalarConverter()

                      @JsonCreator
                      @JvmStatic
                      fun create(rawValue: Any) = UUID(converter.toScalar(rawValue))
                    }
                  }

                  /**
                   * Wrapper that holds all supported scalar types
                   */
                  data class ScalarWrapper(
                    /**
                     * Custom scalar
                     */
                    val custom: CustomScalarTestQuery.UUID
                  )

                  data class Result(
                    /**
                     * Query that returns wrapper object with all supported scalar types
                     */
                    val scalarQuery: CustomScalarTestQuery.ScalarWrapper
                  )
                }
            """.trimIndent()

        val query =
            """
            query CustomScalarTestQuery {
              scalarQuery {
                custom
              }
            }
            """.trimIndent()

        verifyGeneratedFileSpecContents(
            query,
            expected,
            GraphQLClientGeneratorConfig(
                packageName = "com.expediagroup.graphql.plugin.generator.integration",
                customScalarMap = mapOf("UUID" to GraphQLScalar("UUID", "java.util.UUID", "com.expediagroup.graphql.plugin.generator.UUIDScalarConverter"))
            )
        )
    }

    @Test
    fun `verify selection sets can reference custom scalars`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.execute
                import com.expediagroup.graphql.plugin.generator.UUIDScalarConverter
                import com.expediagroup.graphql.types.GraphQLResponse
                import com.fasterxml.jackson.annotation.JsonCreator
                import com.fasterxml.jackson.annotation.JsonValue
                import kotlin.Any
                import kotlin.Int
                import kotlin.String
                import kotlin.jvm.JvmStatic

                const val CUSTOM_SCALAR_TEST_QUERY: String =
                    "query CustomScalarTestQuery {\n  first: scalarQuery {\n    ... scalarSelections\n  }\n  second: scalarQuery {\n    ... scalarSelections\n  }\n}\nfragment scalarSelections on ScalarWrapper {\n  count\n  custom\n  id\n}"

                class CustomScalarTestQuery(
                  private val graphQLClient: GraphQLClient
                ) {
                  suspend fun execute(): GraphQLResponse<CustomScalarTestQuery.Result> =
                      graphQLClient.execute(CUSTOM_SCALAR_TEST_QUERY, "CustomScalarTestQuery", null)

                  /**
                   * Custom scalar representing UUID
                   */
                  data class UUID(
                    val value: java.util.UUID
                  ) {
                    @JsonValue
                    fun rawValue() = converter.toJson(value)

                    companion object {
                      val converter: UUIDScalarConverter = UUIDScalarConverter()

                      @JsonCreator
                      @JvmStatic
                      fun create(rawValue: Any) = UUID(converter.toScalar(rawValue))
                    }
                  }

                  /**
                   * Wrapper that holds all supported scalar types
                   */
                  data class ScalarWrapper(
                    /**
                     * A signed 32-bit nullable integer value
                     */
                    val count: Int?,
                    /**
                     * Custom scalar
                     */
                    val custom: CustomScalarTestQuery.UUID,
                    /**
                     * ID represents unique identifier that is not intended to be human readable
                     */
                    val id: ID
                  )

                  data class Result(
                    /**
                     * Query that returns wrapper object with all supported scalar types
                     */
                    val first: CustomScalarTestQuery.ScalarWrapper,
                    /**
                     * Query that returns wrapper object with all supported scalar types
                     */
                    val second: CustomScalarTestQuery.ScalarWrapper
                  )
                }
            """.trimIndent()

        val query =
            """
            query CustomScalarTestQuery {
              first: scalarQuery {
                ... scalarSelections
              }
              second: scalarQuery {
                ... scalarSelections
              }
            }
            fragment scalarSelections on ScalarWrapper {
              count
              custom
              id
            }
            """.trimIndent()

        verifyGeneratedFileSpecContents(
            query,
            expected,
            GraphQLClientGeneratorConfig(
                packageName = "com.expediagroup.graphql.plugin.generator.integration",
                customScalarMap = mapOf("UUID" to GraphQLScalar("UUID", "java.util.UUID", "com.expediagroup.graphql.plugin.generator.UUIDScalarConverter"))
            )
        )
    }
}
