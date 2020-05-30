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

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorConfig
import com.expediagroup.graphql.plugin.generator.verifyGraphQLClientGeneration
import graphql.language.SelectionSet
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GenerateGraphQLObjectTypeSpecIT {

    @Test
    fun `verify we can generate valid object type spec`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.types.GraphQLResponse
            import kotlin.Boolean
            import kotlin.Int
            import kotlin.String

            const val COMPLEX_OBJECT_TEST_QUERY: String =
                "query ComplexObjectTestQuery {\n  complexObjectQuery {\n    id\n    name\n    optional\n    details {\n      id\n      flag\n      value\n    }\n  }\n}"

            class ComplexObjectTestQuery(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResponse<ComplexObjectTestQuery.Result> =
                  graphQLClient.execute(COMPLEX_OBJECT_TEST_QUERY, "ComplexObjectTestQuery", null)

              /**
               * Inner type object description
               */
              data class DetailsObject(
                /**
                 * Unique identifier
                 */
                val id: Int,
                /**
                 * Boolean flag
                 */
                val flag: Boolean,
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
                 * Optional value
                 * Second line of the description
                 */
                val optional: String?,
                /**
                 * Some additional details
                 */
                val details: ComplexObjectTestQuery.DetailsObject
              )

              data class Result(
                /**
                 * Query returning an object that references another object
                 */
                val complexObjectQuery: ComplexObjectTestQuery.ComplexObject
              )
            }
        """.trimIndent()
        val query = """
            query ComplexObjectTestQuery {
              complexObjectQuery {
                id
                name
                optional
                details {
                  id
                  flag
                  value
                }
              }
            }
        """.trimIndent()
        verifyGraphQLClientGeneration(query, expected)
    }

    @Test
    fun `verify we can generate object using named fragments`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.types.GraphQLResponse
            import kotlin.Int
            import kotlin.String

            const val COMPLEX_OBJECT_QUERY_WITH_NAMED_FRAGMENT: String =
                "query ComplexObjectQueryWithNamedFragment {\n  complexObjectQuery {\n    ...complexObjectFields\n  }\n}\n\nfragment complexObjectFields on ComplexObject {\n  id\n  name\n  details {\n    ...detailObjectFields\n  }\n}\n\nfragment detailObjectFields on DetailsObject {\n  value\n}"

            class ComplexObjectQueryWithNamedFragment(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResponse<ComplexObjectQueryWithNamedFragment.Result> =
                  graphQLClient.execute(COMPLEX_OBJECT_QUERY_WITH_NAMED_FRAGMENT,
                  "ComplexObjectQueryWithNamedFragment", null)

              /**
               * Inner type object description
               */
              data class DetailsObject(
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
                 * Some additional details
                 */
                val details: ComplexObjectQueryWithNamedFragment.DetailsObject
              )

              data class Result(
                /**
                 * Query returning an object that references another object
                 */
                val complexObjectQuery: ComplexObjectQueryWithNamedFragment.ComplexObject
              )
            }
        """.trimIndent()

        val queryWithNamedFragment = """
            query ComplexObjectQueryWithNamedFragment {
              complexObjectQuery {
                ...complexObjectFields
              }
            }

            fragment complexObjectFields on ComplexObject {
              id
              name
              details {
                ...detailObjectFields
              }
            }

            fragment detailObjectFields on DetailsObject {
              value
            }
        """.trimIndent()
        verifyGraphQLClientGeneration(queryWithNamedFragment, expected)
    }

    @Test
    fun `verify object generation will fail if named fragment doesnt exist`() {
        val invalidQuery = """
            query InvalidQueryNamedFragmentDoesntExist {
              complexObjectQuery {
                 ...fragmentThatDoesNotExist
              }
            }
        """.trimIndent()
        assertThrows<RuntimeException> {
            verifyGraphQLClientGeneration(invalidQuery, "will throw exception")
        }
    }

    @Test
    fun `verify exception is thrown when attempting to generate object with empty selection set`() {
        assertThrows<RuntimeException> {
            generateGraphQLObjectTypeSpec(mockk(), mockk(), SelectionSet.newSelectionSet().build())
        }
    }

    @Test
    fun `verify exception is thrown when attempting to select a field that doesnt exist`() {
        val invalidQuery = """
            query InvalidQueryFieldDoesntExist {
              complexObjectQuery {
                 id
                 name
                 doesntExist
              }
            }
        """.trimIndent()
        assertThrows<RuntimeException> {
            verifyGraphQLClientGeneration(invalidQuery, "will throw exception")
        }
    }

    @Test
    fun `verify we can generate object with a list field`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.types.GraphQLResponse
            import kotlin.Int
            import kotlin.String
            import kotlin.collections.List

            const val J_UNIT_TEST_QUERY: String =
                "query JUnitTestQuery {\n  listQuery {\n    id\n    name\n  }\n}"

            class JUnitTestQuery(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResponse<JUnitTestQuery.Result> =
                  graphQLClient.execute(J_UNIT_TEST_QUERY, "JUnitTestQuery", null)

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

              data class Result(
                /**
                 * Query returning list of simple objects
                 */
                val listQuery: List<JUnitTestQuery.BasicObject>
              )
            }
        """.trimIndent()

        val query = """
            query JUnitTestQuery {
              listQuery {
                id
                name
              }
            }
        """.trimIndent()
        verifyGraphQLClientGeneration(query, expected)
    }

    @Test
    fun `verify exception is thrown when attempting to select deprecated field`() {
        val invalidQuery = """
            query DeprecatedFieldQuery {
              deprecatedQuery
            }
        """.trimIndent()
        assertThrows<RuntimeException> {
            verifyGraphQLClientGeneration(invalidQuery, "will throw exception")
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
        verifyGraphQLClientGeneration(
            invalidQuery,
            expected,
            GraphQLClientGeneratorConfig(
                packageName = "com.expediagroup.graphql.plugin.generator.integration",
                allowDeprecated = true
            ))
    }

    @Test
    fun `verify we can generate nested objects`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.types.GraphQLResponse
            import kotlin.Int
            import kotlin.String
            import kotlin.collections.List

            const val NESTED_QUERY: String =
                "query NestedQuery {\n  nestedObjectQuery {\n    id\n    name\n    children {\n      id\n      name\n    }\n  }\n}"

            class NestedQuery(
              private val graphQLClient: GraphQLClient<*>
            ) {
              suspend fun execute(): GraphQLResponse<NestedQuery.Result> = graphQLClient.execute(NESTED_QUERY,
                  "NestedQuery", null)

              /**
               * Example of an object self-referencing itself
               */
              data class NestedObject(
                /**
                 * Unique identifier
                 */
                val id: Int,
                /**
                 * Name of the object
                 */
                val name: String,
                /**
                 * Children elements
                 */
                val children: List<NestedQuery.NestedObject>
              )

              data class Result(
                /**
                 * Query returning object referencing itself
                 */
                val nestedObjectQuery: NestedQuery.NestedObject
              )
            }
        """.trimIndent()
        val nestedQuery = """
            query NestedQuery {
              nestedObjectQuery {
                id
                name
                children {
                  id
                  name
                }
              }
            }
        """.trimIndent()
        verifyGraphQLClientGeneration(nestedQuery, expected)
    }
}
