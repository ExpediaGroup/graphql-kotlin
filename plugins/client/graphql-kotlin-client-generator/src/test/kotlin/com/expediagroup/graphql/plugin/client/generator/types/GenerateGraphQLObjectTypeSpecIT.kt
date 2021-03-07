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

import com.expediagroup.graphql.plugin.client.generator.exceptions.InvalidFragmentException
import com.expediagroup.graphql.plugin.client.generator.exceptions.InvalidSelectionSetException
import com.expediagroup.graphql.plugin.client.generator.verifyGeneratedFileSpecContents
import graphql.language.SelectionSet
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GenerateGraphQLObjectTypeSpecIT {

    @Test
    fun `verify we can generate valid object type spec`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Boolean
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass

                const val COMPLEX_OBJECT_TEST_QUERY: String =
                    "query ComplexObjectTestQuery {\n  complexObjectQuery {\n    id\n    name\n    optional\n    details {\n      id\n      flag\n      value\n    }\n  }\n}"

                class ComplexObjectTestQuery : GraphQLClientRequest<ComplexObjectTestQuery.Result> {
                  override val query: String = COMPLEX_OBJECT_TEST_QUERY

                  override val operationName: String = "ComplexObjectTestQuery"

                  override fun responseType(): KClass<ComplexObjectTestQuery.Result> =
                      ComplexObjectTestQuery.Result::class

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
        val query =
            """
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
        verifyGeneratedFileSpecContents(query, expected)
    }

    @Test
    fun `verify we can generate object using named fragments`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass

                const val COMPLEX_OBJECT_QUERY_WITH_NAMED_FRAGMENT: String =
                    "query ComplexObjectQueryWithNamedFragment {\n  complexObjectQuery {\n    ...complexObjectFields\n  }\n}\n\nfragment complexObjectFields on ComplexObject {\n  id\n  name\n  details {\n    ...detailObjectFields\n  }\n}\n\nfragment detailObjectFields on DetailsObject {\n  value\n}"

                class ComplexObjectQueryWithNamedFragment :
                    GraphQLClientRequest<ComplexObjectQueryWithNamedFragment.Result> {
                  override val query: String = COMPLEX_OBJECT_QUERY_WITH_NAMED_FRAGMENT

                  override val operationName: String = "ComplexObjectQueryWithNamedFragment"

                  override fun responseType(): KClass<ComplexObjectQueryWithNamedFragment.Result> =
                      ComplexObjectQueryWithNamedFragment.Result::class

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

        val queryWithNamedFragment =
            """
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
        verifyGeneratedFileSpecContents(queryWithNamedFragment, expected)
    }

    @Test
    fun `verify object generation will fail if named fragment doesnt exist`() {
        val invalidQuery =
            """
                query InvalidQueryNamedFragmentDoesntExist {
                  complexObjectQuery {
                     ...fragmentThatDoesNotExist
                  }
                }
            """.trimIndent()
        assertThrows<InvalidFragmentException> {
            verifyGeneratedFileSpecContents(invalidQuery, "will throw exception")
        }
    }

    @Test
    fun `verify exception is thrown when attempting to generate object with empty selection set`() {
        assertThrows<InvalidSelectionSetException> {
            generateGraphQLObjectTypeSpec(
                mockk(),
                mockk {
                    every { name } returns "junit_object"
                },
                SelectionSet.newSelectionSet().build()
            )
        }
    }

    @Test
    fun `verify exception is thrown when attempting to select a field that doesnt exist`() {
        val invalidQuery =
            """
                query InvalidQueryFieldDoesntExist {
                  complexObjectQuery {
                     id
                     name
                     doesntExist
                  }
                }
            """.trimIndent()
        assertThrows<InvalidSelectionSetException> {
            verifyGeneratedFileSpecContents(invalidQuery, "will throw exception")
        }
    }

    @Test
    fun `verify we can generate object with a list field`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Int
                import kotlin.String
                import kotlin.collections.List
                import kotlin.reflect.KClass

                const val J_UNIT_TEST_QUERY: String =
                    "query JUnitTestQuery {\n  listQuery {\n    id\n    name\n  }\n}"

                class JUnitTestQuery : GraphQLClientRequest<JUnitTestQuery.Result> {
                  override val query: String = J_UNIT_TEST_QUERY

                  override val operationName: String = "JUnitTestQuery"

                  override fun responseType(): KClass<JUnitTestQuery.Result> = JUnitTestQuery.Result::class

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

        val query =
            """
                query JUnitTestQuery {
                  listQuery {
                    id
                    name
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(query, expected)
    }

    @Test
    fun `verify we can generate self referencing objects`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Int
                import kotlin.String
                import kotlin.collections.List
                import kotlin.reflect.KClass

                const val NESTED_QUERY: String =
                    "query NestedQuery {\n  nestedObjectQuery {\n    id\n    name\n    children {\n      name\n      children {\n        id\n        name\n        children {\n          id\n          name\n        }\n      }\n    }\n  }\n}"

                class NestedQuery : GraphQLClientRequest<NestedQuery.Result> {
                  override val query: String = NESTED_QUERY

                  override val operationName: String = "NestedQuery"

                  override fun responseType(): KClass<NestedQuery.Result> = NestedQuery.Result::class

                  /**
                   * Example of an object self-referencing itself
                   */
                  data class NestedObject4(
                    /**
                     * Unique identifier
                     */
                    val id: Int,
                    /**
                     * Name of the object
                     */
                    val name: String
                  )

                  /**
                   * Example of an object self-referencing itself
                   */
                  data class NestedObject3(
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
                    val children: List<NestedQuery.NestedObject4>
                  )

                  /**
                   * Example of an object self-referencing itself
                   */
                  data class NestedObject2(
                    /**
                     * Name of the object
                     */
                    val name: String,
                    /**
                     * Children elements
                     */
                    val children: List<NestedQuery.NestedObject3>
                  )

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
                    val children: List<NestedQuery.NestedObject2>
                  )

                  data class Result(
                    /**
                     * Query returning object referencing itself
                     */
                    val nestedObjectQuery: NestedQuery.NestedObject
                  )
                }
            """.trimIndent()
        val nestedQuery =
            """
                query NestedQuery {
                  nestedObjectQuery {
                    id
                    name
                    children {
                      name
                      children {
                        id
                        name
                        children {
                          id
                          name
                        }
                      }
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(nestedQuery, expected)
    }

    @Test
    fun `verify we can generate objects with different selection sets`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass

                const val DIFFERENT_SELECTIONS_QUERY: String =
                    "query DifferentSelectionsQuery {\n  first: complexObjectQuery {\n    id\n    name\n  }\n  second: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n}"

                class DifferentSelectionsQuery : GraphQLClientRequest<DifferentSelectionsQuery.Result> {
                  override val query: String = DIFFERENT_SELECTIONS_QUERY

                  override val operationName: String = "DifferentSelectionsQuery"

                  override fun responseType(): KClass<DifferentSelectionsQuery.Result> =
                      DifferentSelectionsQuery.Result::class

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
                    val details: DifferentSelectionsQuery.DetailsObject
                  )

                  data class Result(
                    /**
                     * Query returning an object that references another object
                     */
                    val first: DifferentSelectionsQuery.ComplexObject,
                    /**
                     * Query returning an object that references another object
                     */
                    val second: DifferentSelectionsQuery.ComplexObject2
                  )
                }
            """.trimIndent()
        val differentSelectionsQuery =
            """
                query DifferentSelectionsQuery {
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
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(differentSelectionsQuery, expected)
    }

    @Test
    fun `verify we can generate objects that have different sub-selections`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Boolean
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass

                const val DIFFERENT_SELECTIONS_QUERY: String =
                    "query DifferentSelectionsQuery {\n  first: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n      flag\n    }\n  }\n  second: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n}"

                class DifferentSelectionsQuery : GraphQLClientRequest<DifferentSelectionsQuery.Result> {
                  override val query: String = DIFFERENT_SELECTIONS_QUERY

                  override val operationName: String = "DifferentSelectionsQuery"

                  override fun responseType(): KClass<DifferentSelectionsQuery.Result> =
                      DifferentSelectionsQuery.Result::class

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
                    val value: String,
                    /**
                     * Boolean flag
                     */
                    val flag: Boolean
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
                    val details: DifferentSelectionsQuery.DetailsObject
                  )

                  /**
                   * Inner type object description
                   */
                  data class DetailsObject2(
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
                    val details: DifferentSelectionsQuery.DetailsObject2
                  )

                  data class Result(
                    /**
                     * Query returning an object that references another object
                     */
                    val first: DifferentSelectionsQuery.ComplexObject,
                    /**
                     * Query returning an object that references another object
                     */
                    val second: DifferentSelectionsQuery.ComplexObject2
                  )
                }
            """.trimIndent()
        val differentSelectionsQuery =
            """
                query DifferentSelectionsQuery {
                  first: complexObjectQuery {
                    id
                    name
                    details {
                      id
                      value
                      flag
                    }
                  }
                  second: complexObjectQuery {
                    id
                    name
                    details {
                      id
                      value
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(differentSelectionsQuery, expected)
    }

    @Test
    fun `verify generation fails if query specifies invalid fragment`() {
        val invalidFragmentQuery =
            """
                query InvalidFragmentQuery {
                  complexObjectQuery {
                    ... complexObjectFields
                  }
                }
                fragment complexObjectFields on complex {
                  id
                  name
                }
            """.trimIndent()
        assertThrows<InvalidFragmentException> {
            verifyGeneratedFileSpecContents(invalidFragmentQuery, "will throw exception")
        }
    }
}
