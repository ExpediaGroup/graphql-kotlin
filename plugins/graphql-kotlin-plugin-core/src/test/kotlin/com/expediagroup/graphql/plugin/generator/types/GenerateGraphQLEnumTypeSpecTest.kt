package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.generator.mockContext
import com.expediagroup.graphql.plugin.generator.testSchema
import graphql.language.EnumTypeDefinition
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GenerateGraphQLEnumTypeSpecTest {

    private val testSchema = testSchema()

    @Test
    fun `verify enum types are correctly generated`() {
        val expectedGeneratedEnumType = """
            /**
             * Custom enum description
             */
            enum class CustomEnum {
              /**
               * First enum value
               */
              ONE,

              /**
               * Second enum value
               */
              TWO,

              /**
               * Third enum value
               */
              @kotlin.Deprecated(message = "only goes up to two")
              THREE,

              /**
               * This is a default enum value that will be used when attempting to deserialize unknown value.
               */
              @com.fasterxml.jackson.annotation.JsonEnumDefaultValue
              __UNKNOWN_VALUE
            }""".trimIndent()

        val enumTypeDefinition = testSchema.getType("CustomEnum", EnumTypeDefinition::class.java).get()
        val enumTypeSpec = generateGraphQLEnumTypeSpec(mockContext(), enumTypeDefinition)
        assertEquals(expectedGeneratedEnumType, enumTypeSpec.toString().trim())
    }
}
