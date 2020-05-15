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

import com.expediagroup.graphql.plugin.generator.verifyGraphQLClientGeneration
import org.junit.jupiter.api.Test

class GenerateVariableTypeSpecIT {

    @Test
    fun `verify query with variables is correctly generated`() {
        // KT-2425 workaround to escape $ in string templates - we need to escape the escaped $
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.client.GraphQLResult
            import kotlin.Boolean
            import kotlin.Float
            import kotlin.String

            const val TEST_QUERY_WITH_VARIABLES: String =
                "query TestQueryWithVariables(${'$'}{'${'$'}'}criteria: SimpleArgumentInput) {\n  inputObjectQuery(criteria: ${'$'}{'${'$'}'}criteria)\n}"

            class TestQueryWithVariables(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(variables: TestQueryWithVariables.Variables):
                  GraphQLResult<TestQueryWithVariables.TestQueryWithVariablesResult> =
                  graphQLClient.execute(TEST_QUERY_WITH_VARIABLES, "TestQueryWithVariables", variables)

              data class Variables(
                val criteria: TestQueryWithVariables.SimpleArgumentInput?
              )

              /**
               * Test input object
               */
              data class SimpleArgumentInput(
                /**
                 * Maximum value for test criteria
                 */
                val max: Float?,
                /**
                 * Minimum value for test criteria
                 */
                val min: Float?,
                /**
                 * New value to be set
                 */
                val newName: String?
              )

              data class TestQueryWithVariablesResult(
                /**
                 * Query that accepts some input arguments
                 */
                val inputObjectQuery: Boolean
              )
            }
        """.trimIndent()
        val query = """
            query TestQueryWithVariables(${'$'}criteria: SimpleArgumentInput) {
              inputObjectQuery(criteria: ${'$'}criteria)
            }
        """.trimIndent()
        verifyGraphQLClientGeneration(query, expected)
    }
}
