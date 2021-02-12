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

import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorConfig
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import com.expediagroup.graphql.plugin.client.generator.exceptions.InvalidPolymorphicQueryException
import com.expediagroup.graphql.plugin.client.generator.exceptions.InvalidSelectionSetException
import com.expediagroup.graphql.plugin.client.generator.verifyGeneratedFileSpecContents
import graphql.language.SelectionSet
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GenerateGraphQLInterfaceTypeSpecIT {

    @Test
    fun `verify we can generate valid interface type spec with inline fragments using kotlinx-serialization`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Float
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass
                import kotlinx.serialization.SerialName
                import kotlinx.serialization.Serializable

                const val INTERFACE_WITH_INLINE_FRAGMENTS_TEST_QUERY: String =
                    "query InterfaceWithInlineFragmentsTestQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n}"

                @Serializable
                class InterfaceWithInlineFragmentsTestQuery :
                    GraphQLClientRequest<InterfaceWithInlineFragmentsTestQuery.Result> {
                  override val query: String = INTERFACE_WITH_INLINE_FRAGMENTS_TEST_QUERY

                  override val operationName: String = "InterfaceWithInlineFragmentsTestQuery"

                  override fun responseType(): KClass<InterfaceWithInlineFragmentsTestQuery.Result> =
                      InterfaceWithInlineFragmentsTestQuery.Result::class

                  /**
                   * Example interface implementation where value is an integer
                   */
                  @Serializable
                  @SerialName(value = "FirstInterfaceImplementation")
                  data class FirstInterfaceImplementation(
                    /**
                     * Unique identifier of the first implementation
                     */
                    override val id: Int,
                    /**
                     * Name of the first implementation
                     */
                    override val name: String,
                    /**
                     * Custom field integer value
                     */
                    val intValue: Int
                  ) : InterfaceWithInlineFragmentsTestQuery.BasicInterface()

                  /**
                   * Example interface implementation where value is a float
                   */
                  @Serializable
                  @SerialName(value = "SecondInterfaceImplementation")
                  data class SecondInterfaceImplementation(
                    /**
                     * Unique identifier of the second implementation
                     */
                    override val id: Int,
                    /**
                     * Name of the second implementation
                     */
                    override val name: String,
                    /**
                     * Custom field float value
                     */
                    val floatValue: Float
                  ) : InterfaceWithInlineFragmentsTestQuery.BasicInterface()

                  /**
                   * Very basic interface
                   */
                  @Serializable
                  sealed class BasicInterface {
                    /**
                     * Unique identifier of an interface
                     */
                    abstract val id: Int

                    /**
                     * Name field
                     */
                    abstract val name: String
                  }

                  @Serializable
                  data class Result(
                    /**
                     * Query returning an interface
                     */
                    val interfaceQuery: InterfaceWithInlineFragmentsTestQuery.BasicInterface
                  )
                }
            """.trimIndent()

        val query =
            """
                query InterfaceWithInlineFragmentsTestQuery {
                  interfaceQuery {
                    __typename
                    id
                    name
                    ... on FirstInterfaceImplementation {
                      intValue
                    }
                    ... on SecondInterfaceImplementation {
                      floatValue
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(query, expected)
    }

    @Test
    fun `verify we can generate valid interface type spec with named fragments`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Float
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass
                import kotlinx.serialization.SerialName
                import kotlinx.serialization.Serializable

                const val INTERFACE_WITH_NAMED_FRAGMENTS_TEST_QUERY: String =
                    "query InterfaceWithNamedFragmentsTestQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... firstInterfaceImplFields\n    ... secondInterfaceImplFields\n  }\n}\n\nfragment firstInterfaceImplFields on FirstInterfaceImplementation {\n  id\n  name\n  intValue\n}\nfragment secondInterfaceImplFields on SecondInterfaceImplementation {\n  id\n  name\n  floatValue\n}"

                @Serializable
                class InterfaceWithNamedFragmentsTestQuery :
                    GraphQLClientRequest<InterfaceWithNamedFragmentsTestQuery.Result> {
                  override val query: String = INTERFACE_WITH_NAMED_FRAGMENTS_TEST_QUERY

                  override val operationName: String = "InterfaceWithNamedFragmentsTestQuery"

                  override fun responseType(): KClass<InterfaceWithNamedFragmentsTestQuery.Result> =
                      InterfaceWithNamedFragmentsTestQuery.Result::class

                  /**
                   * Example interface implementation where value is an integer
                   */
                  @Serializable
                  @SerialName(value = "FirstInterfaceImplementation")
                  data class FirstInterfaceImplementation(
                    /**
                     * Unique identifier of the first implementation
                     */
                    override val id: Int,
                    /**
                     * Name of the first implementation
                     */
                    override val name: String,
                    /**
                     * Custom field integer value
                     */
                    val intValue: Int
                  ) : InterfaceWithNamedFragmentsTestQuery.BasicInterface()

                  /**
                   * Example interface implementation where value is a float
                   */
                  @Serializable
                  @SerialName(value = "SecondInterfaceImplementation")
                  data class SecondInterfaceImplementation(
                    /**
                     * Unique identifier of the second implementation
                     */
                    override val id: Int,
                    /**
                     * Name of the second implementation
                     */
                    override val name: String,
                    /**
                     * Custom field float value
                     */
                    val floatValue: Float
                  ) : InterfaceWithNamedFragmentsTestQuery.BasicInterface()

                  /**
                   * Very basic interface
                   */
                  @Serializable
                  sealed class BasicInterface {
                    /**
                     * Unique identifier of an interface
                     */
                    abstract val id: Int

                    /**
                     * Name field
                     */
                    abstract val name: String
                  }

                  @Serializable
                  data class Result(
                    /**
                     * Query returning an interface
                     */
                    val interfaceQuery: InterfaceWithNamedFragmentsTestQuery.BasicInterface
                  )
                }
            """.trimIndent()

        val queryWithNamedFragments =
            """
                query InterfaceWithNamedFragmentsTestQuery {
                  interfaceQuery {
                    __typename
                    id
                    name
                    ... firstInterfaceImplFields
                    ... secondInterfaceImplFields
                  }
                }

                fragment firstInterfaceImplFields on FirstInterfaceImplementation {
                  id
                  name
                  intValue
                }
                fragment secondInterfaceImplFields on SecondInterfaceImplementation {
                  id
                  name
                  floatValue
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(queryWithNamedFragments, expected)
    }

    @Test
    fun `verify interface generation will throw exception if empty selection set is specified`() {
        assertThrows<InvalidSelectionSetException> {
            generateGraphQLInterfaceTypeSpec(
                mockk(),
                mockk {
                    every { name } returns "junit_interface"
                },
                SelectionSet.newSelectionSet().build()
            )
        }
    }

    @Test
    fun `verify interface generation will throw exception if __typename is not selected`() {
        val invalidQuery =
            """
                query InvalidQueryMissingTypeNameSelection {
                  interfaceQuery {
                    id
                    name
                    ... on FirstInterfaceImplementation {
                      intValue
                    }
                    ... on SecondInterfaceImplementation {
                      floatValue
                    }
                  }
                }
            """.trimIndent()
        assertThrows<InvalidPolymorphicQueryException> {
            verifyGeneratedFileSpecContents(invalidQuery, "will throw exception")
        }
    }

    @Test
    fun `verify interface generation will throw exception if not all types are selected`() {
        val invalidQuery =
            """
                query InvalidQueryMissingTypeSelection {
                  interfaceQuery {
                    __typename
                    id
                    name
                    ... on FirstInterfaceImplementation {
                      value
                    }
                  }
                }
            """.trimIndent()
        assertThrows<InvalidPolymorphicQueryException> {
            verifyGeneratedFileSpecContents(invalidQuery, "will throw exception")
        }
    }

    @Test
    fun `verify graphql client generation supports different selection sets between interfaces`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Float
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass
                import kotlinx.serialization.SerialName
                import kotlinx.serialization.Serializable

                const val DIFFERENT_SELECTION_SET_QUERY: String =
                    "query DifferentSelectionSetQuery {\n  first: interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n  second: interfaceQuery {\n    __typename\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n}"

                @Serializable
                class DifferentSelectionSetQuery : GraphQLClientRequest<DifferentSelectionSetQuery.Result> {
                  override val query: String = DIFFERENT_SELECTION_SET_QUERY

                  override val operationName: String = "DifferentSelectionSetQuery"

                  override fun responseType(): KClass<DifferentSelectionSetQuery.Result> =
                      DifferentSelectionSetQuery.Result::class

                  /**
                   * Example interface implementation where value is an integer
                   */
                  @Serializable
                  @SerialName(value = "FirstInterfaceImplementation")
                  data class FirstInterfaceImplementation(
                    /**
                     * Unique identifier of the first implementation
                     */
                    override val id: Int,
                    /**
                     * Name of the first implementation
                     */
                    override val name: String,
                    /**
                     * Custom field integer value
                     */
                    val intValue: Int
                  ) : DifferentSelectionSetQuery.BasicInterface()

                  /**
                   * Example interface implementation where value is a float
                   */
                  @Serializable
                  @SerialName(value = "SecondInterfaceImplementation")
                  data class SecondInterfaceImplementation(
                    /**
                     * Unique identifier of the second implementation
                     */
                    override val id: Int,
                    /**
                     * Name of the second implementation
                     */
                    override val name: String,
                    /**
                     * Custom field float value
                     */
                    val floatValue: Float
                  ) : DifferentSelectionSetQuery.BasicInterface()

                  /**
                   * Very basic interface
                   */
                  @Serializable
                  sealed class BasicInterface {
                    /**
                     * Unique identifier of an interface
                     */
                    abstract val id: Int

                    /**
                     * Name field
                     */
                    abstract val name: String
                  }

                  /**
                   * Example interface implementation where value is an integer
                   */
                  @Serializable
                  @SerialName(value = "FirstInterfaceImplementation")
                  data class FirstInterfaceImplementation2(
                    /**
                     * Name of the first implementation
                     */
                    override val name: String,
                    /**
                     * Custom field integer value
                     */
                    val intValue: Int
                  ) : DifferentSelectionSetQuery.BasicInterface2()

                  /**
                   * Example interface implementation where value is a float
                   */
                  @Serializable
                  @SerialName(value = "SecondInterfaceImplementation")
                  data class SecondInterfaceImplementation2(
                    /**
                     * Name of the second implementation
                     */
                    override val name: String,
                    /**
                     * Custom field float value
                     */
                    val floatValue: Float
                  ) : DifferentSelectionSetQuery.BasicInterface2()

                  /**
                   * Very basic interface
                   */
                  @Serializable
                  sealed class BasicInterface2 {
                    /**
                     * Name field
                     */
                    abstract val name: String
                  }

                  @Serializable
                  data class Result(
                    /**
                     * Query returning an interface
                     */
                    val first: DifferentSelectionSetQuery.BasicInterface,
                    /**
                     * Query returning an interface
                     */
                    val second: DifferentSelectionSetQuery.BasicInterface2
                  )
                }
            """.trimIndent()
        val differentSelectionQuery =
            """
                query DifferentSelectionSetQuery {
                  first: interfaceQuery {
                    __typename
                    id
                    name
                    ... on FirstInterfaceImplementation {
                      intValue
                    }
                    ... on SecondInterfaceImplementation {
                      floatValue
                    }
                  }
                  second: interfaceQuery {
                    __typename
                    name
                    ... on FirstInterfaceImplementation {
                      intValue
                    }
                    ... on SecondInterfaceImplementation {
                      floatValue
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(differentSelectionQuery, expected)
    }

    @Test
    fun `verify graphql client generation supports different selection sets between interface implementations`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Float
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass
                import kotlinx.serialization.SerialName
                import kotlinx.serialization.Serializable

                const val DIFFERENT_SELECTION_SET_QUERY: String =
                    "query DifferentSelectionSetQuery {\n  first: interfaceQuery {\n    __typename\n    id\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n  second: interfaceQuery {\n    __typename\n    id\n    ... on FirstInterfaceImplementation {\n      name\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      name\n      floatValue\n    }\n  }\n}"

                @Serializable
                class DifferentSelectionSetQuery : GraphQLClientRequest<DifferentSelectionSetQuery.Result> {
                  override val query: String = DIFFERENT_SELECTION_SET_QUERY

                  override val operationName: String = "DifferentSelectionSetQuery"

                  override fun responseType(): KClass<DifferentSelectionSetQuery.Result> =
                      DifferentSelectionSetQuery.Result::class

                  /**
                   * Example interface implementation where value is an integer
                   */
                  @Serializable
                  @SerialName(value = "FirstInterfaceImplementation")
                  data class FirstInterfaceImplementation(
                    /**
                     * Unique identifier of the first implementation
                     */
                    override val id: Int,
                    /**
                     * Custom field integer value
                     */
                    val intValue: Int
                  ) : DifferentSelectionSetQuery.BasicInterface()

                  /**
                   * Example interface implementation where value is a float
                   */
                  @Serializable
                  @SerialName(value = "SecondInterfaceImplementation")
                  data class SecondInterfaceImplementation(
                    /**
                     * Unique identifier of the second implementation
                     */
                    override val id: Int,
                    /**
                     * Custom field float value
                     */
                    val floatValue: Float
                  ) : DifferentSelectionSetQuery.BasicInterface()

                  /**
                   * Very basic interface
                   */
                  @Serializable
                  sealed class BasicInterface {
                    /**
                     * Unique identifier of an interface
                     */
                    abstract val id: Int
                  }

                  /**
                   * Example interface implementation where value is an integer
                   */
                  @Serializable
                  @SerialName(value = "FirstInterfaceImplementation")
                  data class FirstInterfaceImplementation2(
                    /**
                     * Unique identifier of the first implementation
                     */
                    override val id: Int,
                    /**
                     * Name of the first implementation
                     */
                    val name: String,
                    /**
                     * Custom field integer value
                     */
                    val intValue: Int
                  ) : DifferentSelectionSetQuery.BasicInterface2()

                  /**
                   * Example interface implementation where value is a float
                   */
                  @Serializable
                  @SerialName(value = "SecondInterfaceImplementation")
                  data class SecondInterfaceImplementation2(
                    /**
                     * Unique identifier of the second implementation
                     */
                    override val id: Int,
                    /**
                     * Name of the second implementation
                     */
                    val name: String,
                    /**
                     * Custom field float value
                     */
                    val floatValue: Float
                  ) : DifferentSelectionSetQuery.BasicInterface2()

                  /**
                   * Very basic interface
                   */
                  @Serializable
                  sealed class BasicInterface2 {
                    /**
                     * Unique identifier of an interface
                     */
                    abstract val id: Int
                  }

                  @Serializable
                  data class Result(
                    /**
                     * Query returning an interface
                     */
                    val first: DifferentSelectionSetQuery.BasicInterface,
                    /**
                     * Query returning an interface
                     */
                    val second: DifferentSelectionSetQuery.BasicInterface2
                  )
                }
            """.trimIndent()
        val differentSelectionQuery =
            """
                query DifferentSelectionSetQuery {
                  first: interfaceQuery {
                    __typename
                    id
                    ... on FirstInterfaceImplementation {
                      intValue
                    }
                    ... on SecondInterfaceImplementation {
                      floatValue
                    }
                  }
                  second: interfaceQuery {
                    __typename
                    id
                    ... on FirstInterfaceImplementation {
                      name
                      intValue
                    }
                    ... on SecondInterfaceImplementation {
                      name
                      floatValue
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(differentSelectionQuery, expected)
    }

    @Test
    fun `verify we can generate valid interface type spec with inline fragments using jackson`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import com.fasterxml.jackson.annotation.JsonSubTypes
                import com.fasterxml.jackson.annotation.JsonTypeInfo
                import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
                import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
                import kotlin.Float
                import kotlin.Int
                import kotlin.String
                import kotlin.reflect.KClass

                const val INTERFACE_WITH_INLINE_FRAGMENTS_TEST_QUERY: String =
                    "query InterfaceWithInlineFragmentsTestQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n}"

                class InterfaceWithInlineFragmentsTestQuery :
                    GraphQLClientRequest<InterfaceWithInlineFragmentsTestQuery.Result> {
                  override val query: String = INTERFACE_WITH_INLINE_FRAGMENTS_TEST_QUERY

                  override val operationName: String = "InterfaceWithInlineFragmentsTestQuery"

                  override fun responseType(): KClass<InterfaceWithInlineFragmentsTestQuery.Result> =
                      InterfaceWithInlineFragmentsTestQuery.Result::class

                  /**
                   * Example interface implementation where value is an integer
                   */
                  data class FirstInterfaceImplementation(
                    /**
                     * Unique identifier of the first implementation
                     */
                    override val id: Int,
                    /**
                     * Name of the first implementation
                     */
                    override val name: String,
                    /**
                     * Custom field integer value
                     */
                    val intValue: Int
                  ) : InterfaceWithInlineFragmentsTestQuery.BasicInterface

                  /**
                   * Example interface implementation where value is a float
                   */
                  data class SecondInterfaceImplementation(
                    /**
                     * Unique identifier of the second implementation
                     */
                    override val id: Int,
                    /**
                     * Name of the second implementation
                     */
                    override val name: String,
                    /**
                     * Custom field float value
                     */
                    val floatValue: Float
                  ) : InterfaceWithInlineFragmentsTestQuery.BasicInterface

                  /**
                   * Very basic interface
                   */
                  @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.PROPERTY,
                    property = "__typename"
                  )
                  @JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                      InterfaceWithInlineFragmentsTestQuery.FirstInterfaceImplementation::class,
                      name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value
                      = InterfaceWithInlineFragmentsTestQuery.SecondInterfaceImplementation::class,
                      name="SecondInterfaceImplementation")])
                  interface BasicInterface {
                    /**
                     * Unique identifier of an interface
                     */
                    abstract val id: Int

                    /**
                     * Name field
                     */
                    abstract val name: String
                  }

                  data class Result(
                    /**
                     * Query returning an interface
                     */
                    val interfaceQuery: InterfaceWithInlineFragmentsTestQuery.BasicInterface
                  )
                }
            """.trimIndent()

        val query =
            """
                query InterfaceWithInlineFragmentsTestQuery {
                  interfaceQuery {
                    __typename
                    id
                    name
                    ... on FirstInterfaceImplementation {
                      intValue
                    }
                    ... on SecondInterfaceImplementation {
                      floatValue
                    }
                  }
                }
            """.trimIndent()
        verifyGeneratedFileSpecContents(
            query,
            expected,
            GraphQLClientGeneratorConfig(
                packageName = "com.expediagroup.graphql.plugin.generator.integration",
                serializer = GraphQLSerializer.JACKSON
            )
        )
    }
}
