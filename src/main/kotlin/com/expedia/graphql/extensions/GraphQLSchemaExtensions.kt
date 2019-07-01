package com.expedia.graphql.extensions

import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter

fun GraphQLSchema.print(
    includeScalarTypes: Boolean = true,
    includeExtendedScalarTypes: Boolean = true,
    includeIntrospectionTypes: Boolean = true,
    includeSchemaDefinition: Boolean = true,
    includeDirectives: Boolean = true
): String {
    val schemaPrinter = SchemaPrinter(
        SchemaPrinter.Options.defaultOptions()
            .includeScalarTypes(includeScalarTypes)
            .includeExtendedScalarTypes(includeExtendedScalarTypes)
            .includeIntrospectionTypes(includeIntrospectionTypes)
            .includeSchemaDefintion(includeSchemaDefinition)
            .includeDirectives(false)
    )

    var schemaString = schemaPrinter.print(this)
    if (includeDirectives) {
        val directivesToString = this.directives.joinToString("\n\n") { directive -> """
                #${directive.description}
                directive @${directive.name} on ${directive.validLocations().joinToString(" | ") { loc -> loc.name }}
            """.trimIndent()
        }
        schemaString += "\n" + directivesToString
    }
    return schemaString
}
