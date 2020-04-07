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
import graphql.language.SelectionSet
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GenerateGraphQLInterfaceTypeSpecIT {

    @Test
    fun `verify we can generate valid interface type spec with inline fragments`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.client.GraphQLResult
            import com.fasterxml.jackson.annotation.JsonSubTypes
            import com.fasterxml.jackson.annotation.JsonTypeInfo
            import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
            import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
            import kotlin.Float
            import kotlin.Int
            import kotlin.String

            const val INTERFACE_WITH_INLINE_FRAGMENTS_TEST_QUERY: String =
                "query InterfaceWithInlineFragmentsTestQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n}"

            class InterfaceWithInlineFragmentsTestQuery(
              private val graphQLClient: GraphQLClient
            ) {
              suspend fun interfaceWithInlineFragmentsTestQuery():
                  GraphQLResult<InterfaceWithInlineFragmentsTestQuery.InterfaceWithInlineFragmentsTestQueryResult>
                  = graphQLClient.executeOperation(INTERFACE_WITH_INLINE_FRAGMENTS_TEST_QUERY,
                  "InterfaceWithInlineFragmentsTestQuery", null)

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
                val id: Int

                /**
                 * Name field
                 */
                val name: String
              }

              data class InterfaceWithInlineFragmentsTestQueryResult(
                /**
                 * Query returning an interface
                 */
                val interfaceQuery: InterfaceWithInlineFragmentsTestQuery.BasicInterface?
              )
            }
        """.trimIndent()

        val query = """
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
        verifyGraphQLClientGeneration(query, expected)
    }

    @Test
    fun `verify we can generate valid interface type spec with named fragments`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.integration

            import com.expediagroup.graphql.client.GraphQLClient
            import com.expediagroup.graphql.client.GraphQLResult
            import com.fasterxml.jackson.annotation.JsonSubTypes
            import com.fasterxml.jackson.annotation.JsonTypeInfo
            import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
            import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
            import kotlin.Float
            import kotlin.Int
            import kotlin.String

            const val INTERFACE_WITH_NAMED_FRAGMENTS_TEST_QUERY: String =
                "query InterfaceWithNamedFragmentsTestQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... firstInterfaceImplFields\n    ... secondInterfaceImplFields\n  }\n}\n\nfragment firstInterfaceImplFields on FirstInterfaceImplementation {\n  id\n  name\n  intValue\n}\nfragment secondInterfaceImplFields on SecondInterfaceImplementation {\n  id\n  name\n  floatValue\n}"

            class InterfaceWithNamedFragmentsTestQuery(
              private val graphQLClient: GraphQLClient
            ) {
              suspend fun interfaceWithNamedFragmentsTestQuery():
                  GraphQLResult<InterfaceWithNamedFragmentsTestQuery.InterfaceWithNamedFragmentsTestQueryResult>
                  = graphQLClient.executeOperation(INTERFACE_WITH_NAMED_FRAGMENTS_TEST_QUERY,
                  "InterfaceWithNamedFragmentsTestQuery", null)

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
              ) : InterfaceWithNamedFragmentsTestQuery.BasicInterface

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
              ) : InterfaceWithNamedFragmentsTestQuery.BasicInterface

              /**
               * Very basic interface
               */
              @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.PROPERTY,
                property = "__typename"
              )
              @JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
                  InterfaceWithNamedFragmentsTestQuery.FirstInterfaceImplementation::class,
                  name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value
                  = InterfaceWithNamedFragmentsTestQuery.SecondInterfaceImplementation::class,
                  name="SecondInterfaceImplementation")])
              interface BasicInterface {
                /**
                 * Unique identifier of an interface
                 */
                val id: Int

                /**
                 * Name field
                 */
                val name: String
              }

              data class InterfaceWithNamedFragmentsTestQueryResult(
                /**
                 * Query returning an interface
                 */
                val interfaceQuery: InterfaceWithNamedFragmentsTestQuery.BasicInterface?
              )
            }
        """.trimIndent()

        val queryWithNamedFragments = """
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
        verifyGraphQLClientGeneration(queryWithNamedFragments, expected)
    }

    @Test
    fun `verify interface generation will throw exception if empty selection set is specified`() {
        assertThrows<RuntimeException> {
            generateGraphQLInterfaceTypeSpec(mockk(), mockk(), SelectionSet.newSelectionSet().build())
        }
    }

    @Test
    fun `verify interface generation will throw exception if __typename is not selected`() {
        val invalidQuery = """
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
        assertThrows<RuntimeException> {
            verifyGraphQLClientGeneration(invalidQuery, "will throw exception")
        }
    }

    @Test
    fun `verify interface generation will throw exception if not all types are selected`() {
        val invalidQuery = """
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
        assertThrows<RuntimeException> {
            verifyGraphQLClientGeneration(invalidQuery, "will throw exception")
        }
    }
}
