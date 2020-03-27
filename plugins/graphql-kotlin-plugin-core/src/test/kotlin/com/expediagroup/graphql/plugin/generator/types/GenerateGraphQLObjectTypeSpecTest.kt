package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.verifyTypeSpecGeneration
import graphql.language.SelectionSet
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GenerateGraphQLObjectTypeSpecTest {
    private val expectedJunitTestQueryResult = """
        class JunitTestQueryResult {
          /**
           * Inner type object description
           */
          data class DetailsObject(
            /**
             * Unique identifier
             */
            val id: kotlin.Int,
            /**
             * Boolean flag
             */
            val flag: kotlin.Boolean,
            /**
             * Actual detail value
             */
            val value: kotlin.String
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
            val id: kotlin.Int,
            /**
             * Some object name
             */
            val name: kotlin.String,
            /**
             * Optional value
             * Second line of the description.
             */
            val optional: kotlin.String?,
            /**
             * Some additional details
             */
            val details: com.expediagroup.graphql.plugin.generator.types.test.JunitTestQueryResult.DetailsObject
          )

          data class JunitTestQueryResult(
            /**
             * Query returning an object that references another object
             */
            val complexObjectQuery: com.expediagroup.graphql.plugin.generator.types.test.JunitTestQueryResult.ComplexObject?
          )
        }
    """.trimIndent()

    @Test
    fun `verify we can generate valid object type spec`() {
        val query = """
            query JUnitTestQuery {
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
        verifyTypeSpecGeneration(query, expectedJunitTestQueryResult)
    }

    @Test
    fun `verify we can generate object using named fragments`() {
        val queryWithNamedFragment = """
            query JUnitTestQuery {
              complexObjectQuery {
                ...complexObjectFields
              }
            }

            fragment complexObjectFields on ComplexObject {
              id
              name
              optional
              details {
                ...detailObjectFields
              }
            }

            fragment detailObjectFields on DetailsObject {
              id
              flag
              value
            }
        """.trimIndent()
        verifyTypeSpecGeneration(queryWithNamedFragment, expectedJunitTestQueryResult)
    }

    @Test
    fun `verify object generation will fail if named fragment doesnt exist`() {
        val invalidQuery = """
            query JUnitTestQuery {
              complexObjectQuery {
                 ...fragmentThatDoesNotExist
              }
            }
        """.trimIndent()
        assertThrows<RuntimeException> {
            verifyTypeSpecGeneration(invalidQuery, "will throw exception")
        }
    }

    @Test
    fun `verify exception is thrown when attempting to generate object with empty selection set`() {
        assertThrows<RuntimeException> {
            generateGraphQLObjectTypeSpec(mockk(), mockk(), SelectionSet.newSelectionSet().build())
        }
    }
}
