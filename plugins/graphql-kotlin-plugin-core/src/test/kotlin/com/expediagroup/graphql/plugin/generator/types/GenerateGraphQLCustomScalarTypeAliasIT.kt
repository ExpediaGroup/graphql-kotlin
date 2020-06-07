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

import com.expediagroup.graphql.plugin.generator.generateTestFileSpec
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GenerateGraphQLCustomScalarTypeAliasIT {

    @Test
    fun `verify can generate type aliases for GraphQL ID and custom scalars`() {
        val expectedGraphQLAliasTypeSpec = """
            package com.expediagroup.graphql.plugin.generator.integration

            import kotlin.String

            typealias ID = String

            /**
             * Custom scalar representing UUID
             */
            typealias UUID = String
        """.trimIndent()
        val expectedQueryFileSpec = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.types.GraphQLResponse
            import kotlin.String

            const val SCALAR_ALIAS_TEST_QUERY: String =
                "query ScalarAliasTestQuery {\n  scalarQuery {\n    id\n    custom\n  }\n}"

            class ScalarAliasTestQuery(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResponse<ScalarAliasTestQuery.Result> =
                  graphQLClient.execute(SCALAR_ALIAS_TEST_QUERY, "ScalarAliasTestQuery", null)

              /**
               * Wrapper that holds all supported scalar types
               */
              data class ScalarWrapper(
                /**
                 * ID represents unique identifier that is not intended to be human readable
                 */
                val id: ID,
                /**
                 * Custom scalar
                 */
                val custom: UUID
              )

              data class Result(
                /**
                 * Query that returns wrapper object with all supported scalar types
                 */
                val scalarQuery: ScalarAliasTestQuery.ScalarWrapper
              )
            }
        """.trimIndent()

        val query = """
            query ScalarAliasTestQuery {
              scalarQuery {
                id
                custom
              }
            }
        """.trimIndent()
        val fileSpecs = generateTestFileSpec(query)
        assertEquals(2, fileSpecs.size)
        assertEquals(expectedQueryFileSpec, fileSpecs[0].toString().trim())
        assertEquals(expectedGraphQLAliasTypeSpec, fileSpecs[1].toString().trim())
    }
}
