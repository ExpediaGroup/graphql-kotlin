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

import com.expediagroup.graphql.plugin.generator.GraphQLClientGenerator
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorConfig
import com.expediagroup.graphql.plugin.generator.testSchema
import com.expediagroup.graphql.plugin.generator.verifyGeneratedFileSpecContents
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

class GenerateGraphQLClientIT {

    @Test
    fun `verify generated client does not change the operation name`() {
        val expectedQueryFileSpec = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.types.GraphQLResponse
            import kotlin.String

            const val MI_XE_DCA_SE_QUERY: String = "query miXEDcaSEQuery {\n  scalarQuery {\n    name\n  }\n}"

            class MiXEDcaSEQuery(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResponse<MiXEDcaSEQuery.Result> =
                  graphQLClient.execute(MI_XE_DCA_SE_QUERY, "miXEDcaSEQuery", null)

              /**
               * Wrapper that holds all supported scalar types
               */
              data class ScalarWrapper(
                /**
                 * UTF-8 character sequence
                 */
                val name: String
              )

              data class Result(
                /**
                 * Query that returns wrapper object with all supported scalar types
                 */
                val scalarQuery: MiXEDcaSEQuery.ScalarWrapper
              )
            }
        """.trimIndent()

        val query = """
            query miXEDcaSEQuery {
              scalarQuery {
                name
              }
            }
        """.trimIndent()
        verifyGeneratedFileSpecContents(query, expectedQueryFileSpec)
    }

    @Test
    fun `verify generated client does not require operation name`(@TempDir tempDir: Path) {
        val expectedQueryFileSpec = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.types.GraphQLResponse
            import kotlin.String

            const val ANONYMOUS_TEST_QUERY: String = "query {\n  scalarQuery {\n    name\n  }\n}"

            class AnonymousTestQuery(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResponse<AnonymousTestQuery.Result> =
                  graphQLClient.execute(ANONYMOUS_TEST_QUERY, null, null)

              /**
               * Wrapper that holds all supported scalar types
               */
              data class ScalarWrapper(
                /**
                 * UTF-8 character sequence
                 */
                val name: String
              )

              data class Result(
                /**
                 * Query that returns wrapper object with all supported scalar types
                 */
                val scalarQuery: AnonymousTestQuery.ScalarWrapper
              )
            }
        """.trimIndent()

        val query = """
            query {
              scalarQuery {
                name
              }
            }
        """.trimIndent()
        val testDirectory = tempDir.toFile()
        val queryFile = File(testDirectory, "anonymousTestQuery.graphql")
        queryFile.deleteOnExit()
        queryFile.writeText(query)

        val generator = GraphQLClientGenerator(testSchema(), GraphQLClientGeneratorConfig(packageName = "com.expediagroup.graphql.plugin.generator.integration"))
        val fileSpecs = generator.generate(listOf(queryFile))
        assertEquals(1, fileSpecs.size)
        assertEquals(expectedQueryFileSpec, fileSpecs[0].toString().trim())
    }

    @Test
    fun `verify we can generate objects using aliases`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.types.GraphQLResponse
            import kotlin.Boolean
            import kotlin.String

            const val ALIAS_TEST_QUERY: String =
                "query AliasTestQuery {\n  first: inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n  second: inputObjectQuery(criteria: { min: 5.0, max: 10.0 } )\n}"

            class AliasTestQuery(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResponse<AliasTestQuery.Result> =
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
        verifyGeneratedFileSpecContents(query, expected)
    }

    @Test
    fun `verify exception is thrown when attempting to select deprecated field`() {
        val invalidQuery = """
            query DeprecatedFieldQuery {
              deprecatedQuery
            }
        """.trimIndent()
        assertThrows<RuntimeException> {
            verifyGeneratedFileSpecContents(invalidQuery, "will throw exception")
        }
    }

    @Test
    fun `verify object with deprecated fields is generated if explicitly configured`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.types.GraphQLResponse
            import kotlin.Deprecated
            import kotlin.String

            const val DEPRECATED_FIELD_QUERY: String = "query DeprecatedFieldQuery {\n  deprecatedQuery\n}"

            class DeprecatedFieldQuery(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResponse<DeprecatedFieldQuery.Result> =
                  graphQLClient.execute(DEPRECATED_FIELD_QUERY, "DeprecatedFieldQuery", null)

              data class Result(
                /**
                 * Deprecated query that should not be used anymore
                 */
                @Deprecated(message = "old query should not be used")
                val deprecatedQuery: String
              )
            }
        """.trimIndent()

        val invalidQuery = """
            query DeprecatedFieldQuery {
              deprecatedQuery
            }
        """.trimIndent()
        verifyGeneratedFileSpecContents(
            invalidQuery,
            expected,
            GraphQLClientGeneratorConfig(
                packageName = "com.expediagroup.graphql.plugin.generator.integration",
                allowDeprecated = true
            ))
    }
}
