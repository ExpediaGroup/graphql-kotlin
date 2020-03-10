package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.squareup.kotlinpoet.FileSpec
import graphql.language.Field
import graphql.language.ObjectTypeDefinition
import graphql.language.SelectionSet
import graphql.schema.idl.SchemaParser
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class GenerateObjectTypeSpecTest {

    @Test
    fun `verify we can generate valid object type spec`() {
        val sdl = """
            type Query {
              objectTestQuery: MyCustomObject
            }

            "Custom type description"
            type MyCustomObject {
              "Some unique identifier"
              id: Int!,
              "Some object name"
              name: String!,
              "Optional value"
              optional: String
            }
        """.trimIndent()
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

        val schema = SchemaParser().parse(sdl)
        val objectTypeDefinition = schema.getType("MyCustomObject", ObjectTypeDefinition::class.java).get()

        val ctx = GraphQLClientGeneratorContext(
            packageName = "com.expediagroup.graphql.plugin.generator.types.test",
            graphQLSchema = schema,
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
        val sdl = """
            type Query {
              objectTestQuery: MyCustomObject
            }

            "Custom type description"
            type MyCustomObject {
              "Some unique identifier"
              id: Int!,
              "Some object name"
              name: String!,
              "Optional value"
              optional: String,
              "Some additional details"
              details: MyDetailsObject
            }

            "Inner type object description"
            type MyDetailsObject {
              "Unique identifier"
              id: Int!,
              "Boolean flag"
              flag: Boolean!,
              "Actual detail value"
              value: String!
            }
        """.trimIndent()
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

        val schema = SchemaParser().parse(sdl)
        val objectTypeDefinition = schema.getType("MyCustomObject", ObjectTypeDefinition::class.java).get()

        val ctx = GraphQLClientGeneratorContext(
            packageName = "com.expediagroup.graphql.plugin.generator.types.test",
            graphQLSchema = schema,
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
