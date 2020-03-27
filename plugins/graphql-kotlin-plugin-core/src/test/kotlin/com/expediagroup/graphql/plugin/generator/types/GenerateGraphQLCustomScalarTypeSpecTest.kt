package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.client.converters.CustomScalarConverter
import com.expediagroup.graphql.plugin.generator.CustomScalarConverterMapping
import com.expediagroup.graphql.plugin.generator.mockContext
import graphql.language.Description
import graphql.language.ScalarTypeDefinition
import graphql.language.SourceLocation
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GenerateGraphQLCustomScalarTypeSpecTest {

    @Test
    fun `verify can generate custom scalar with converter mapping`() {
        val expected = """
            /**
             * custom UUID scalar
             */
            class UUID(
              val value: java.util.UUID
            ) {
              @com.fasterxml.jackson.annotation.JsonValue
              fun rawValue() = converter.toJson(value)

              companion object {
                val converter: com.expediagroup.graphql.plugin.generator.UUIDConverter = com.expediagroup.graphql.plugin.generator.UUIDConverter()

                @com.fasterxml.jackson.annotation.JsonCreator
                @kotlin.jvm.JvmStatic
                fun create(rawValue: kotlin.String) = UUID(converter.toScalar(rawValue))
              }
            }
        """.trimIndent()

        val customScalarDefinition = ScalarTypeDefinition.newScalarTypeDefinition()
            .name("UUID")
            .description(Description("custom UUID scalar", SourceLocation(0, 0), false))
            .build()
        val scalarTypeSpec = generateGraphQLCustomScalarTypeSpec(
            mockContext(scalarTypeToConverterMapping = mapOf("UUID" to CustomScalarConverterMapping("java.util.UUID", "com.expediagroup.graphql.plugin.generator.UUIDConverter"))),
            customScalarDefinition
        )
        assertEquals(expected, scalarTypeSpec.toString().trim())
    }
}
