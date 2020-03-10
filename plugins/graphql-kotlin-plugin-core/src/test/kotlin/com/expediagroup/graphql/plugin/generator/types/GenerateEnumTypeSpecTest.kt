package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.FileSpec
import graphql.language.EnumTypeDefinition
import graphql.schema.idl.SchemaParser
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class GenerateEnumTypeSpecTest {

    @Test
    fun `verify enum types are correctly generated`() {
        val expectedGeneratedEnumType = """
            package com.expediagroup.graphql.plugin.generator.types.test

            import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

            /**
             * Custom enum description
             */
            enum class MyCustomEnum {
              /**
               * First enum value
               */
              ONE,

              /**
               * Second enum value
               */
              TWO,

              /**
               * This is a default enum value that will be used when attempting to deserialize unknown value.
               */
              @JsonEnumDefaultValue
              __UNKNOWN_VALUE
            }
        """.trimIndent()

        val sdl = """
            type Query {
              enumTestQuery: MyCustomEnum
            }

            "Custom enum description"
            enum MyCustomEnum {
              "First enum value"
              ONE,
              "Second enum value"
              TWO
            }
        """.trimIndent()
        val schema = SchemaParser().parse(sdl)
        val enumTypeDefinition = schema.getType("MyCustomEnum", EnumTypeDefinition::class.java).get()

        val ctx = GraphQLClientGeneratorContext(
            packageName = "com.expediagroup.graphql.plugin.generator.types.test",
            graphQLSchema = schema,
            rootType = "EnumQueryTest",
            queryDocument = mockk()
        )
        val enumTypeSpec = generateEnumTypeSpec(ctx, enumTypeDefinition)
        val fileSpec = FileSpec.builder(packageName = ctx.packageName, fileName = ctx.rootType)
            .addType(enumTypeSpec)
            .build()

        val result = StringWriter()
        fileSpec.writeTo(result)
        assertEquals(expectedGeneratedEnumType, result.toString().trim())
    }
}
