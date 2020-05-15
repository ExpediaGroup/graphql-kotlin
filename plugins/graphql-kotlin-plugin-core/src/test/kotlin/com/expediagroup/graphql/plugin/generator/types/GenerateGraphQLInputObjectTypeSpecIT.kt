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

class GenerateGraphQLInputObjectTypeSpecIT {

    @Test
    fun `verify we can generate valid input object type spec`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.client.GraphQLResult
            import kotlin.Boolean
            import kotlin.String

            const val INPUT_OBJECT_TEST_QUERY: String =
                "query InputObjectTestQuery {\n  inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n}"

            class InputObjectTestQuery(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResult<InputObjectTestQuery.Result> =
                  graphQLClient.execute(INPUT_OBJECT_TEST_QUERY, "InputObjectTestQuery", null)

              data class Result(
                /**
                 * Query that accepts some input arguments
                 */
                val inputObjectQuery: Boolean
              )
            }
        """.trimIndent()

        val query = """
            query InputObjectTestQuery {
              inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )
            }
        """.trimIndent()
        verifyGraphQLClientGeneration(query, expected)
    }

    @Test
    fun `verify we can generate objects using aliases`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.client.GraphQLResult
            import kotlin.Boolean
            import kotlin.String

            const val ALIAS_TEST_QUERY: String =
                "query AliasTestQuery {\n  first: inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n  second: inputObjectQuery(criteria: { min: 5.0, max: 10.0 } )\n}"

            class AliasTestQuery(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResult<AliasTestQuery.Result> =
                  graphQLClient.execute(ALIAS_TEST_QUERY, "AliasTestQuery", null)

              data class Result(
                /**
                 * Query that accepts some input arguments
                 */
                val first: Boolean,
                /**
                 * Query that accepts some input arguments
                 */
                val second: Boolean
              )
            }
        """.trimIndent()

        val query = """
            query AliasTestQuery {
              first: inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )
              second: inputObjectQuery(criteria: { min: 5.0, max: 10.0 } )
            }
        """.trimIndent()
        verifyGraphQLClientGeneration(query, expected)
    }
}
