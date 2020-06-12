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

import com.expediagroup.graphql.plugin.generator.verifyGeneratedFileSpecContents
import graphql.language.SelectionSet
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GenerateGraphQLUnionTypeSpecIT {

    @Test
    fun `verify we can generate union type using inline fragments`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.types.GraphQLResponse
                import com.fasterxml.jackson.annotation.JsonSubTypes
                import com.fasterxml.jackson.annotation.JsonTypeInfo
                import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
                import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
                import kotlin.Int
                import kotlin.String

                const val UNION_QUERY_WITH_INLINE_FRAGMENTS: String =
                    "query UnionQueryWithInlineFragments {\n  unionQuery {\n    __typename\n    ... on BasicObject {\n      id\n      name\n    }\n    ... on ComplexObject {\n      id\n      name\n      optional\n    }\n  }\n}"

                class UnionQueryWithInlineFragments(
                  private val graphQLClient: GraphQLClient<*>
                ) {
                  suspend fun execute(): GraphQLResponse<UnionQueryWithInlineFragments.Result> =
                      graphQLClient.execute(UNION_QUERY_WITH_INLINE_FRAGMENTS, "UnionQueryWithInlineFragments",
                      null)

                  /**
                   * Some basic description
                   */
                  data class BasicObject(
                    val id: Int,
                    /**
                     * Object name
                     */
                    val name: String
                  ) : UnionQueryWithInlineFragments.BasicUnion

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
                    val optional: String?
                  ) : UnionQueryWithInlineFragments.BasicUnion

                  /**
                   * Very basic union of BasicObject and ComplexObject
                   */
                  @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.PROPERTY,
                    property = "__typename"
                  )
                  @JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      UnionQueryWithInlineFragments.BasicObject::class,
                      name="BasicObject"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      UnionQueryWithInlineFragments.ComplexObject::class, name="ComplexObject")])
                  interface BasicUnion

                  data class Result(
                    /**
                     * Query returning union
                     */
                    val unionQuery: UnionQueryWithInlineFragments.BasicUnion
                  )
                }
            """.trimIndent()
        val queryWithInlineFragments =
            """
                query UnionQueryWithInlineFragments {
                  unionQuery {
                    __typename
                    ... on BasicObject {
                      id
                      name
                    }
                    ... on ComplexObject {
                      id
                      name
                      optional
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(queryWithInlineFragments, expected)
    }

    @Test
    fun `verify we can generate union type using named fragments`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.types.GraphQLResponse
                import com.fasterxml.jackson.annotation.JsonSubTypes
                import com.fasterxml.jackson.annotation.JsonTypeInfo
                import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
                import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
                import kotlin.Int
                import kotlin.String

                const val UNION_QUERY_WITH_NAMED_FRAGMENTS: String =
                    "query UnionQueryWithNamedFragments {\n  unionQuery {\n    ... basicObjectFields\n    ... complexObjectFields\n  }\n}\n\nfragment basicObjectFields on BasicObject {\n  __typename\n  id\n  name\n}\nfragment complexObjectFields on ComplexObject {\n  __typename\n  id\n  name\n  optional\n}"

                class UnionQueryWithNamedFragments(
                  private val graphQLClient: GraphQLClient<*>
                ) {
                  suspend fun execute(): GraphQLResponse<UnionQueryWithNamedFragments.Result> =
                      graphQLClient.execute(UNION_QUERY_WITH_NAMED_FRAGMENTS, "UnionQueryWithNamedFragments", null)

                  /**
                   * Some basic description
                   */
                  data class BasicObject(
                    val id: Int,
                    /**
                     * Object name
                     */
                    val name: String
                  ) : UnionQueryWithNamedFragments.BasicUnion

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
                    val optional: String?
                  ) : UnionQueryWithNamedFragments.BasicUnion

                  /**
                   * Very basic union of BasicObject and ComplexObject
                   */
                  @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.PROPERTY,
                    property = "__typename"
                  )
                  @JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      UnionQueryWithNamedFragments.BasicObject::class,
                      name="BasicObject"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      UnionQueryWithNamedFragments.ComplexObject::class, name="ComplexObject")])
                  interface BasicUnion

                  data class Result(
                    /**
                     * Query returning union
                     */
                    val unionQuery: UnionQueryWithNamedFragments.BasicUnion
                  )
                }
            """.trimIndent()
        val queryWithNamedFragments =
            """
                query UnionQueryWithNamedFragments {
                  unionQuery {
                    ... basicObjectFields
                    ... complexObjectFields
                  }
                }

                fragment basicObjectFields on BasicObject {
                  __typename
                  id
                  name
                }
                fragment complexObjectFields on ComplexObject {
                  __typename
                  id
                  name
                  optional
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(queryWithNamedFragments, expected)
    }

    @Test
    fun `verify union type generation will throw exception if not all implementations are selected`() {
        val invalidQuery =
            """
                query InvalidQueryMissingTypeSelection {
                  unionQuery {
                    ... on BasicObject {
                      __typename
                      id
                      name
                    }
                  }
                }
            """.trimIndent()
        assertThrows<RuntimeException> {
            verifyGeneratedFileSpecContents(invalidQuery, "should throw exception")
        }
    }

    @Test
    fun `verify union type generation will throw exception if __typename is not selected`() {
        val invalidQuery =
            """
                query InvalidQueryMissingTypename {
                  unionQuery {
                    ... on BasicObject {
                      id
                      name
                    }
                    ... on ComplexObject {
                      id
                      name
                      optional
                    }
                  }
                }
            """.trimIndent()
        assertThrows<RuntimeException> {
            verifyGeneratedFileSpecContents(invalidQuery, "should throw exception")
        }
    }

    @Test
    fun `verify union type generation will throw exception if we pass empty selection set`() {
        assertThrows<RuntimeException> {
            generateGraphQLUnionTypeSpec(mockk(), mockk(), SelectionSet.newSelectionSet().build())
        }
    }

    @Test
    fun `verify graphql client generation supports different selection sets between union type and concrete type`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.types.GraphQLResponse
                import com.fasterxml.jackson.annotation.JsonSubTypes
                import com.fasterxml.jackson.annotation.JsonTypeInfo
                import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
                import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
                import kotlin.Int
                import kotlin.String

                const val DIFFERENT_SELECTION_SET_QUERY: String =
                    "query DifferentSelectionSetQuery {\n  unionQuery {\n    __typename\n    ... on BasicObject {\n      id\n      name\n    }\n    ... on ComplexObject {\n      id\n      name\n      optional\n    }\n  }\n  complexObjectQuery {\n    id\n    name\n    details {\n      value\n    }\n  }\n}"

                class DifferentSelectionSetQuery(
                  private val graphQLClient: GraphQLClient<*>
                ) {
                  suspend fun execute(): GraphQLResponse<DifferentSelectionSetQuery.Result> =
                      graphQLClient.execute(DIFFERENT_SELECTION_SET_QUERY, "DifferentSelectionSetQuery", null)

                  /**
                   * Some basic description
                   */
                  data class BasicObject(
                    val id: Int,
                    /**
                     * Object name
                     */
                    val name: String
                  ) : DifferentSelectionSetQuery.BasicUnion

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
                    val optional: String?
                  ) : DifferentSelectionSetQuery.BasicUnion

                  /**
                   * Very basic union of BasicObject and ComplexObject
                   */
                  @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.PROPERTY,
                    property = "__typename"
                  )
                  @JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      DifferentSelectionSetQuery.BasicObject::class,
                      name="BasicObject"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      DifferentSelectionSetQuery.ComplexObject::class, name="ComplexObject")])
                  interface BasicUnion

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
                    val details: DifferentSelectionSetQuery.DetailsObject
                  )

                  data class Result(
                    /**
                     * Query returning union
                     */
                    val unionQuery: DifferentSelectionSetQuery.BasicUnion,
                    /**
                     * Query returning an object that references another object
                     */
                    val complexObjectQuery: DifferentSelectionSetQuery.ComplexObject2
                  )
                }
            """.trimIndent()
        val differentSelectionQuery =
            """
                query DifferentSelectionSetQuery {
                  unionQuery {
                    __typename
                    ... on BasicObject {
                      id
                      name
                    }
                    ... on ComplexObject {
                      id
                      name
                      optional
                    }
                  }
                  complexObjectQuery {
                    id
                    name
                    details {
                      value
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(differentSelectionQuery, expected)
    }

    @Test
    fun `verify graphql client generation supports different selection sets between unions`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.types.GraphQLResponse
                import com.fasterxml.jackson.annotation.JsonSubTypes
                import com.fasterxml.jackson.annotation.JsonTypeInfo
                import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
                import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
                import kotlin.Int
                import kotlin.String

                const val DIFFERENT_SELECTION_SET_QUERY: String =
                    "query DifferentSelectionSetQuery {\n  first: unionQuery {\n    __typename\n    ... on BasicObject {\n      id\n    }\n    ... on ComplexObject {\n      id\n    }\n  }\n  second: unionQuery {\n    __typename\n    ... on BasicObject {\n      name\n    }\n    ... on ComplexObject {\n      name\n    }\n  }\n}"

                class DifferentSelectionSetQuery(
                  private val graphQLClient: GraphQLClient<*>
                ) {
                  suspend fun execute(): GraphQLResponse<DifferentSelectionSetQuery.Result> =
                      graphQLClient.execute(DIFFERENT_SELECTION_SET_QUERY, "DifferentSelectionSetQuery", null)

                  /**
                   * Some basic description
                   */
                  data class BasicObject(
                    val id: Int
                  ) : DifferentSelectionSetQuery.BasicUnion

                  /**
                   * Multi line description of a complex type.
                   * This is a second line of the paragraph.
                   * This is final line of the description.
                   */
                  data class ComplexObject(
                    /**
                     * Some unique identifier
                     */
                    val id: Int
                  ) : DifferentSelectionSetQuery.BasicUnion

                  /**
                   * Very basic union of BasicObject and ComplexObject
                   */
                  @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.PROPERTY,
                    property = "__typename"
                  )
                  @JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      DifferentSelectionSetQuery.BasicObject::class,
                      name="BasicObject"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      DifferentSelectionSetQuery.ComplexObject::class, name="ComplexObject")])
                  interface BasicUnion

                  /**
                   * Some basic description
                   */
                  data class BasicObject2(
                    /**
                     * Object name
                     */
                    val name: String
                  ) : DifferentSelectionSetQuery.BasicUnion2

                  /**
                   * Multi line description of a complex type.
                   * This is a second line of the paragraph.
                   * This is final line of the description.
                   */
                  data class ComplexObject2(
                    /**
                     * Some object name
                     */
                    val name: String
                  ) : DifferentSelectionSetQuery.BasicUnion2

                  /**
                   * Very basic union of BasicObject and ComplexObject
                   */
                  @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.PROPERTY,
                    property = "__typename"
                  )
                  @JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      DifferentSelectionSetQuery.BasicObject2::class,
                      name="BasicObject"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      DifferentSelectionSetQuery.ComplexObject2::class, name="ComplexObject")])
                  interface BasicUnion2

                  data class Result(
                    /**
                     * Query returning union
                     */
                    val first: DifferentSelectionSetQuery.BasicUnion,
                    /**
                     * Query returning union
                     */
                    val second: DifferentSelectionSetQuery.BasicUnion2
                  )
                }
            """.trimIndent()
        val differentSelectionQuery =
            """
                query DifferentSelectionSetQuery {
                  first: unionQuery {
                    __typename
                    ... on BasicObject {
                      id
                    }
                    ... on ComplexObject {
                      id
                    }
                  }
                  second: unionQuery {
                    __typename
                    ... on BasicObject {
                      name
                    }
                    ... on ComplexObject {
                      name
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(differentSelectionQuery, expected)
    }
}
