package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.mockContext
import graphql.language.InputObjectTypeDefinition
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GenerateGraphQLInputObjectTypeSpecTest {

    @Test
    fun `verify we can generate valid input object type spec`() {
        val context = mockContext()
        val expected = """
            /**
             * Test input object
             */
            data class SimpleInput(
              /**
               * Minimum value for test criteria
               */
              val min: kotlin.Float?,
              /**
               * Maximum value for test criteria
               */
              val max: kotlin.Float?
            )
        """.trimIndent()

        val inputObjectTypeDefinition = context.graphQLSchema.getType("SimpleInput", InputObjectTypeDefinition::class.java).get()
        val objectTypeSpec = generateGraphQLInputObjectTypeSpec(mockContext(), inputObjectTypeDefinition)
        assertEquals(expected, objectTypeSpec.toString().trim())
    }
}
