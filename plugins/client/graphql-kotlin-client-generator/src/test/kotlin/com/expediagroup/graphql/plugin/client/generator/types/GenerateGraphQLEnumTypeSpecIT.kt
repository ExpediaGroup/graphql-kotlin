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

import com.expediagroup.graphql.plugin.client.generator.verifyGeneratedFileSpecContents
import org.junit.jupiter.api.Test

class GenerateGraphQLEnumTypeSpecIT {

    @Test
    fun `verify enum types are correctly generated`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.execute
                import com.expediagroup.graphql.types.GraphQLResponse
                import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
                import kotlin.Deprecated
                import kotlin.String

                const val ENUM_TEST_QUERY: String = "query EnumTestQuery {\n  enumQuery\n}"

                class EnumTestQuery(
                  private val graphQLClient: GraphQLClient
                ) {
                  suspend fun execute(): GraphQLResponse<EnumTestQuery.Result> =
                      graphQLClient.execute(ENUM_TEST_QUERY, "EnumTestQuery", null)

                  /**
                   * Custom enum description
                   */
                  enum class CustomEnum {
                    /**
                     * First enum value
                     */
                    ONE,

                    /**
                     * Third enum value
                     */
                    @Deprecated(message = "only goes up to two")
                    THREE,

                    /**
                     * Second enum value
                     */
                    TWO,

                    /**
                     * This is a default enum value that will be used when attempting to deserialize unknown value.
                     */
                    @JsonEnumDefaultValue
                    __UNKNOWN_VALUE
                  }

                  data class Result(
                    /**
                     * Query that returns enum value
                     */
                    val enumQuery: EnumTestQuery.CustomEnum
                  )
                }
            """.trimIndent()

        val query =
            """
            query EnumTestQuery {
              enumQuery
            }
            """.trimIndent()

        verifyGeneratedFileSpecContents(query, expected)
    }
}
