package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.verifyTypeSpecGeneration
import graphql.language.SelectionSet
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GenerateGraphQLInterfaceTypeSpecTest {

    private val expectedJunitTestQueryResult = """
        class JunitTestQueryResult {
          /**
           * Example interface implementation where value is an integer
           */
          data class FirstInterfaceImplementation(
            /**
             * Unique identifier of the first implementation
             */
            override val id: kotlin.Int,
            /**
             * Name of the first implementation
             */
            override val name: kotlin.String,
            /**
             * Custom field integer value
             */
            val intValue: kotlin.Int
          ) : com.expediagroup.graphql.plugin.generator.types.test.JunitTestQueryResult.BasicInterface

          /**
           * Example interface implementation where value is a float
           */
          data class SecondInterfaceImplementation(
            /**
             * Unique identifier of the second implementation
             */
            override val id: kotlin.Int,
            /**
             * Name of the second implementation
             */
            override val name: kotlin.String,
            /**
             * Custom field float value
             */
            val floatValue: kotlin.Float
          ) : com.expediagroup.graphql.plugin.generator.types.test.JunitTestQueryResult.BasicInterface

          /**
           * Very basic interface
           */
          @com.fasterxml.jackson.annotation.JsonTypeInfo(
            use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id,
            include = com.fasterxml.jackson.annotation.JsonTypeInfo.As,
            property = "__typename"
          )
          @com.fasterxml.jackson.annotation.JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = com.expediagroup.graphql.plugin.generator.types.test.JunitTestQueryResult.FirstInterfaceImplementation::class, name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = com.expediagroup.graphql.plugin.generator.types.test.JunitTestQueryResult.SecondInterfaceImplementation::class, name="SecondInterfaceImplementation")])
          interface BasicInterface {
            /**
             * Unique identifier of an interface
             */
            val id: kotlin.Int

            /**
             * Name field
             */
            val name: kotlin.String
          }

          data class JunitTestQueryResult(
            /**
             * Query returning an interface
             */
            val interfaceQuery: com.expediagroup.graphql.plugin.generator.types.test.JunitTestQueryResult.BasicInterface?
          )
        }
    """.trimIndent()

    @Test
    fun `verify we can generate valid interface type spec with inline fragments`() {
        val query = """
            query InvalidQueryMissingTypeSelection {
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
        verifyTypeSpecGeneration(query, expectedJunitTestQueryResult)
    }

    @Test
    fun `verify we can generate valid interface type spec with named fragments`() {
        val queryWithNamedFragments = """
            query InvalidQueryMissingTypeSelection {
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
        verifyTypeSpecGeneration(queryWithNamedFragments, expectedJunitTestQueryResult)
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
            query InvalidQueryMissingTypeSelection {
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
            verifyTypeSpecGeneration(invalidQuery, "will throw exception")
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
            verifyTypeSpecGeneration(invalidQuery, "will throw exception")
        }
    }
}
