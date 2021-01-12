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

import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGenerator
import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorConfig
import com.expediagroup.graphql.plugin.client.generator.GraphQLClientType
import com.expediagroup.graphql.plugin.client.generator.exceptions.DeprecatedFieldsSelectedException
import com.expediagroup.graphql.plugin.client.generator.testSchema
import com.expediagroup.graphql.plugin.client.generator.verifyGeneratedFileSpecContents
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

class GenerateGraphQLClientIT {

    @Test
    fun `verify generated client does not change the operation name`() {
        val expectedQueryFileSpec =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.execute
                import com.expediagroup.graphql.types.GraphQLResponse
                import kotlin.String

                const val MI_XE_DCA_SE_QUERY: String = "query miXEDcaSEQuery {\n  scalarQuery {\n    name\n  }\n}"

                class MiXEDcaSEQuery(
                  private val graphQLClient: GraphQLClient
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

        val query =
            """
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
        val expectedQueryFileSpec =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.execute
                import com.expediagroup.graphql.types.GraphQLResponse
                import kotlin.String

                const val ANONYMOUS_TEST_QUERY: String = "query {\n  scalarQuery {\n    name\n  }\n}"

                class AnonymousTestQuery(
                  private val graphQLClient: GraphQLClient
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

        val query =
            """
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
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.execute
                import com.expediagroup.graphql.types.GraphQLResponse
                import kotlin.Boolean
                import kotlin.String

                const val ALIAS_TEST_QUERY: String =
                    "query AliasTestQuery {\n  first: inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n  second: inputObjectQuery(criteria: { min: 5.0, max: 10.0 } )\n}"

                class AliasTestQuery(
                  private val graphQLClient: GraphQLClient
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

        val query =
            """
                query AliasTestQuery {
                  first: inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )
                  second: inputObjectQuery(criteria: { min: 5.0, max: 10.0 } )
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(query, expected)
    }

    @Test
    fun `verify exception is thrown when attempting to select deprecated field`() {
        val invalidQuery =
            """
                query DeprecatedFieldQuery {
                  deprecatedQuery
                }
            """.trimIndent()
        assertThrows<DeprecatedFieldsSelectedException> {
            verifyGeneratedFileSpecContents(invalidQuery, "will throw exception")
        }
    }

    @Test
    fun `verify object with deprecated fields is generated if explicitly configured`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.execute
                import com.expediagroup.graphql.types.GraphQLResponse
                import kotlin.Deprecated
                import kotlin.String

                const val DEPRECATED_FIELD_QUERY: String = "query DeprecatedFieldQuery {\n  deprecatedQuery\n}"

                class DeprecatedFieldQuery(
                  private val graphQLClient: GraphQLClient
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

        val invalidQuery =
            """
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
            )
        )
    }

    @Test
    fun `verify we can generate ktor based client`(@TempDir tempDir: Path) {
        val expectedQueryFileSpec =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLKtorClient
                import com.expediagroup.graphql.types.GraphQLResponse
                import io.ktor.client.request.HttpRequestBuilder
                import kotlin.String
                import kotlin.Unit

                const val ANONYMOUS_TEST_QUERY: String = "query {\n  scalarQuery {\n    name\n  }\n}"

                class AnonymousTestQuery(
                  private val graphQLClient: GraphQLKtorClient<*>
                ) {
                  suspend fun execute(requestBuilder: HttpRequestBuilder.() -> Unit = {}):
                      GraphQLResponse<AnonymousTestQuery.Result> = graphQLClient.execute(ANONYMOUS_TEST_QUERY, null,
                      null, requestBuilder)

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

        val query =
            """
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

        val generator = GraphQLClientGenerator(
            testSchema(),
            GraphQLClientGeneratorConfig(
                packageName = "com.expediagroup.graphql.plugin.generator.integration",
                clientType = GraphQLClientType.KTOR
            )
        )
        val fileSpecs = generator.generate(listOf(queryFile))
        assertEquals(1, fileSpecs.size)
        assertEquals(expectedQueryFileSpec, fileSpecs[0].toString().trim())
    }

    @Test
    fun `verify we can generate spring webclient based client`(@TempDir tempDir: Path) {
        val expectedQueryFileSpec =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLWebClient
                import com.expediagroup.graphql.types.GraphQLResponse
                import kotlin.String
                import kotlin.Unit
                import org.springframework.web.reactive.function.client.WebClient

                const val ANONYMOUS_TEST_QUERY: String = "query {\n  scalarQuery {\n    name\n  }\n}"

                class AnonymousTestQuery(
                  private val graphQLClient: GraphQLWebClient
                ) {
                  suspend fun execute(requestBuilder: WebClient.RequestBodyUriSpec.() -> Unit = {}):
                      GraphQLResponse<AnonymousTestQuery.Result> = graphQLClient.execute(ANONYMOUS_TEST_QUERY, null,
                      null, requestBuilder)

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

        val query =
            """
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

        val generator = GraphQLClientGenerator(
            testSchema(),
            GraphQLClientGeneratorConfig(
                packageName = "com.expediagroup.graphql.plugin.generator.integration",
                clientType = GraphQLClientType.WEBCLIENT
            )
        )
        val fileSpecs = generator.generate(listOf(queryFile))
        assertEquals(1, fileSpecs.size)
        assertEquals(expectedQueryFileSpec, fileSpecs[0].toString().trim())
    }

    @Test
    fun `verifies types are reused with same selection set`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.execute
                import com.expediagroup.graphql.types.GraphQLResponse
                import kotlin.Int
                import kotlin.String

                const val REUSED_TYPES_QUERY: String =
                    "query ReusedTypesQuery {\n  first: complexObjectQuery {\n    id\n    name\n  }\n  second: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n  third: complexObjectQuery {\n    id\n    name\n    details {\n      id\n    }\n  }\n  fourth: complexObjectQuery {\n    id\n    name\n  }\n  fifth: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n}"

                class ReusedTypesQuery(
                  private val graphQLClient: GraphQLClient
                ) {
                  suspend fun execute(): GraphQLResponse<ReusedTypesQuery.Result> =
                      graphQLClient.execute(REUSED_TYPES_QUERY, "ReusedTypesQuery", null)

                  /**
                   * Multi line description of a complex type.
                   * This is a second line of the paragraph.
                   * This is final line of the description.
                   */
                  data class ComplexObject(
                    /**
                     * Some unique identifier
                     */
                    val id: Int,
                    /**
                     * Some object name
                     */
                    val name: String
                  )

                  /**
                   * Inner type object description
                   */
                  data class DetailsObject(
                    /**
                     * Unique identifier
                     */
                    val id: Int,
                    /**
                     * Actual detail value
                     */
                    val value: String
                  )

                  /**
                   * Multi line description of a complex type.
                   * This is a second line of the paragraph.
                   * This is final line of the description.
                   */
                  data class ComplexObject2(
                    /**
                     * Some unique identifier
                     */
                    val id: Int,
                    /**
                     * Some object name
                     */
                    val name: String,
                    /**
                     * Some additional details
                     */
                    val details: ReusedTypesQuery.DetailsObject
                  )

                  /**
                   * Inner type object description
                   */
                  data class DetailsObject2(
                    /**
                     * Unique identifier
                     */
                    val id: Int
                  )

                  /**
                   * Multi line description of a complex type.
                   * This is a second line of the paragraph.
                   * This is final line of the description.
                   */
                  data class ComplexObject3(
                    /**
                     * Some unique identifier
                     */
                    val id: Int,
                    /**
                     * Some object name
                     */
                    val name: String,
                    /**
                     * Some additional details
                     */
                    val details: ReusedTypesQuery.DetailsObject2
                  )

                  data class Result(
                    /**
                     * Query returning an object that references another object
                     */
                    val first: ReusedTypesQuery.ComplexObject,
                    /**
                     * Query returning an object that references another object
                     */
                    val second: ReusedTypesQuery.ComplexObject2,
                    /**
                     * Query returning an object that references another object
                     */
                    val third: ReusedTypesQuery.ComplexObject3,
                    /**
                     * Query returning an object that references another object
                     */
                    val fourth: ReusedTypesQuery.ComplexObject,
                    /**
                     * Query returning an object that references another object
                     */
                    val fifth: ReusedTypesQuery.ComplexObject2
                  )
                }
            """.trimIndent()
        val reusedTypesQuery =
            """
                query ReusedTypesQuery {
                  first: complexObjectQuery {
                    id
                    name
                  }
                  second: complexObjectQuery {
                    id
                    name
                    details {
                      id
                      value
                    }
                  }
                  third: complexObjectQuery {
                    id
                    name
                    details {
                      id
                    }
                  }
                  fourth: complexObjectQuery {
                    id
                    name
                  }
                  fifth: complexObjectQuery {
                    id
                    name
                    details {
                      id
                      value
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(reusedTypesQuery, expected)
    }

    @Test
    fun `verifies list types are reused with same selection set`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.execute
                import com.expediagroup.graphql.types.GraphQLResponse
                import kotlin.Int
                import kotlin.String
                import kotlin.collections.List

                const val REUSED_LIST_TYPES_QUERY: String =
                    "query ReusedListTypesQuery {\n  first: listQuery {\n    id\n    name\n  }\n  second: listQuery {\n    name\n  }\n  third: listQuery {\n    id\n    name\n  }\n  firstComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      id\n      name\n    }\n  }\n  secondComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      id\n      name\n    }\n  }\n  thirdComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      name\n    }\n  }\n  fourthComplex: complexObjectQuery {\n    id\n    basicList {\n      id\n    }\n  }\n}"

                class ReusedListTypesQuery(
                  private val graphQLClient: GraphQLClient
                ) {
                  suspend fun execute(): GraphQLResponse<ReusedListTypesQuery.Result> =
                      graphQLClient.execute(REUSED_LIST_TYPES_QUERY, "ReusedListTypesQuery", null)

                  /**
                   * Some basic description
                   */
                  data class BasicObject(
                    val id: Int,
                    /**
                     * Object name
                     */
                    val name: String
                  )

                  /**
                   * Some basic description
                   */
                  data class BasicObject2(
                    /**
                     * Object name
                     */
                    val name: String
                  )

                  /**
                   * Multi line description of a complex type.
                   * This is a second line of the paragraph.
                   * This is final line of the description.
                   */
                  data class ComplexObject(
                    /**
                     * Some unique identifier
                     */
                    val id: Int,
                    /**
                     * Some object name
                     */
                    val name: String,
                    /**
                     * List of objects
                     */
                    val basicList: List<ReusedListTypesQuery.BasicObject>
                  )

                  /**
                   * Multi line description of a complex type.
                   * This is a second line of the paragraph.
                   * This is final line of the description.
                   */
                  data class ComplexObject2(
                    /**
                     * Some unique identifier
                     */
                    val id: Int,
                    /**
                     * Some object name
                     */
                    val name: String,
                    /**
                     * List of objects
                     */
                    val basicList: List<ReusedListTypesQuery.BasicObject2>
                  )

                  /**
                   * Some basic description
                   */
                  data class BasicObject3(
                    val id: Int
                  )

                  /**
                   * Multi line description of a complex type.
                   * This is a second line of the paragraph.
                   * This is final line of the description.
                   */
                  data class ComplexObject3(
                    /**
                     * Some unique identifier
                     */
                    val id: Int,
                    /**
                     * List of objects
                     */
                    val basicList: List<ReusedListTypesQuery.BasicObject3>
                  )

                  data class Result(
                    /**
                     * Query returning list of simple objects
                     */
                    val first: List<ReusedListTypesQuery.BasicObject>,
                    /**
                     * Query returning list of simple objects
                     */
                    val second: List<ReusedListTypesQuery.BasicObject2>,
                    /**
                     * Query returning list of simple objects
                     */
                    val third: List<ReusedListTypesQuery.BasicObject>,
                    /**
                     * Query returning an object that references another object
                     */
                    val firstComplex: ReusedListTypesQuery.ComplexObject,
                    /**
                     * Query returning an object that references another object
                     */
                    val secondComplex: ReusedListTypesQuery.ComplexObject,
                    /**
                     * Query returning an object that references another object
                     */
                    val thirdComplex: ReusedListTypesQuery.ComplexObject2,
                    /**
                     * Query returning an object that references another object
                     */
                    val fourthComplex: ReusedListTypesQuery.ComplexObject3
                  )
                }
            """.trimIndent()
        val reusedTypesQuery =
            """
                query ReusedListTypesQuery {
                  first: listQuery {
                    id
                    name
                  }
                  second: listQuery {
                    name
                  }
                  third: listQuery {
                    id
                    name
                  }
                  firstComplex: complexObjectQuery {
                    id
                    name
                    basicList {
                      id
                      name
                    }
                  }
                  secondComplex: complexObjectQuery {
                    id
                    name
                    basicList {
                      id
                      name
                    }
                  }
                  thirdComplex: complexObjectQuery {
                    id
                    name
                    basicList {
                      name
                    }
                  }
                  fourthComplex: complexObjectQuery {
                    id
                    basicList {
                      id
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(reusedTypesQuery, expected)
    }
}
