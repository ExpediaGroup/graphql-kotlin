package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.generator.testSchema
import com.squareup.kotlinpoet.FileSpec
import graphql.language.InputObjectTypeDefinition
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class GenerateInputObjectTypeSpecTest {

    @Test
    fun `verify we can generate valid input object type spec`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.types.test

            import kotlin.Float

            /**
             * Test input object
             */
            data class TestCriteriaInput(
              /**
               * Minimum value for test criteria
               */
              val min: Float?,
              /**
               * Maximum value for test criteria
               */
              val max: Float?
            )
        """.trimIndent()

        val inputObjectTypeDefinition = testSchema.getType("TestCriteriaInput", InputObjectTypeDefinition::class.java).get()

        val ctx = GraphQLClientGeneratorContext(
            packageName = "com.expediagroup.graphql.plugin.generator.types.test",
            graphQLSchema = testSchema,
            rootType = "InputObjectQueryTest",
            queryDocument = mockk()
        )
        val objectTypeSpec = generateGraphQLInputObjectTypeSpec(ctx, inputObjectTypeDefinition)
        val fileSpec = FileSpec.builder(packageName = ctx.packageName, fileName = ctx.rootType)
            .addType(objectTypeSpec)
            .build()

        val result = StringWriter()
        fileSpec.writeTo(result)
        assertEquals(expected, result.toString().trim())
    }
}
