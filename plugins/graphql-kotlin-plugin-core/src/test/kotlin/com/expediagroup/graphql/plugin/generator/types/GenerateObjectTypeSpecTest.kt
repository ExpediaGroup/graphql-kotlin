package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.generator.testSchema
import com.squareup.kotlinpoet.FileSpec
import graphql.language.Field
import graphql.language.ObjectTypeDefinition
import graphql.language.SelectionSet
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class GenerateObjectTypeSpecTest {

    @Test
    fun `verify we can generate valid object type spec`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.types.test

            import kotlin.Int
            import kotlin.String

            /**
             * Custom type description
             */
            data class MyCustomObject(
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
               */
              val optional: String?
            )
        """.trimIndent()

        val objectTypeDefinition = testSchema.getType("MyCustomObject", ObjectTypeDefinition::class.java).get()

        val ctx = GraphQLClientGeneratorContext(
            packageName = "com.expediagroup.graphql.plugin.generator.types.test",
            graphQLSchema = testSchema,
            rootType = "ObjectQueryTest",
            queryDocument = mockk()
        )
        val selectionSet: SelectionSet = SelectionSet.newSelectionSet()
            .selection(Field("id"))
            .selection(Field("name"))
            .selection(Field("optional"))
            .build()
        val objectTypeSpec = generateObjectTypeSpec(ctx, objectTypeDefinition, selectionSet)
        val fileSpec = FileSpec.builder(packageName = ctx.packageName, fileName = ctx.rootType)
            .addType(objectTypeSpec)
            .build()

        val result = StringWriter()
        fileSpec.writeTo(result)
        assertEquals(expected, result.toString().trim())
    }

    @Test
    fun `verify we can generate objects referencing other objects`() {
        val expected = """
            package com.expediagroup.graphql.plugin.generator.types.test

            import kotlin.Boolean
            import kotlin.Int
            import kotlin.String

            /**
             * Inner type object description
             */
            data class MyDetailsObject(
              /**
               * Unique identifier
               */
              val id: Int,
              /**
               * Boolean flag
               */
              val flag: Boolean,
              /**
               * Actual detail value
               */
              val value: String
            )

            /**
             * Custom type description
             */
            data class MyCustomObject(
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
              val details: ObjectQueryTest.MyDetailsObject?
            )
        """.trimIndent()

        val objectTypeDefinition = testSchema.getType("MyCustomObject", ObjectTypeDefinition::class.java).get()

        val ctx = GraphQLClientGeneratorContext(
            packageName = "com.expediagroup.graphql.plugin.generator.types.test",
            graphQLSchema = testSchema,
            rootType = "ObjectQueryTest",
            queryDocument = mockk()
        )
        val selectionSet: SelectionSet = SelectionSet.newSelectionSet()
            .selection(Field("id"))
            .selection(Field("name"))
            .selection(Field("details", SelectionSet.newSelectionSet()
                .selection(Field("id"))
                .selection(Field("flag"))
                .selection(Field("value"))
                .build()))
            .build()
        generateObjectTypeSpec(ctx, objectTypeDefinition, selectionSet)
        val fileSpec = FileSpec.builder(packageName = ctx.packageName, fileName = ctx.rootType)
        ctx.typeSpecs.forEach {
            fileSpec.addType(it)
        }

        val result = StringWriter()
        fileSpec.build().writeTo(result)
        assertEquals(expected, result.toString().trim())
    }
}
