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

class GenerateVariableTypeSpecIT {

    @Test
    fun `verify query with variables is correctly generated`() {
        // KT-2425 workaround to escape $ in string templates - we need to escape the escaped $
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Boolean
                import kotlin.Float
                import kotlin.String
                import kotlin.reflect.KClass

                const val TEST_QUERY_WITH_VARIABLES: String =
                    "query TestQueryWithVariables(${'$'}{'${'$'}'}criteria: SimpleArgumentInput) {\n  inputObjectQuery(criteria: ${'$'}{'${'$'}'}criteria)\n}"

                class TestQueryWithVariables(
                  override val variables: TestQueryWithVariables.Variables
                ) : GraphQLClientRequest<TestQueryWithVariables.Result> {
                  override val query: String = TEST_QUERY_WITH_VARIABLES

                  override val operationName: String = "TestQueryWithVariables"

                  override fun responseType(): KClass<TestQueryWithVariables.Result> =
                      TestQueryWithVariables.Result::class

                  data class Variables(
                    val criteria: TestQueryWithVariables.SimpleArgumentInput? = null
                  )

                  data class Result(
                    /**
                     * Query that accepts some input arguments
                     */
                    val inputObjectQuery: Boolean
                  )

                  /**
                   * Test input object
                   */
                  data class SimpleArgumentInput(
                    /**
                     * Maximum value for test criteria
                     */
                    val max: Float? = null,
                    /**
                     * Minimum value for test criteria
                     */
                    val min: Float? = null,
                    /**
                     * New value to be set
                     */
                    val newName: String? = null
                  )
                }
            """.trimIndent()
        val query =
            """
                query TestQueryWithVariables(${'$'}criteria: SimpleArgumentInput) {
                  inputObjectQuery(criteria: ${'$'}criteria)
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(query, expected)
    }
}
