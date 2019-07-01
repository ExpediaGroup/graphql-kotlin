package com.expedia.graphql.extensions

import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter

private val schemaPrinter = SchemaPrinter(
    SchemaPrinter.Options.defaultOptions()
        .includeScalarTypes(true)
        .includeExtendedScalarTypes(true)
        .includeIntrospectionTypes(true)
        .includeSchemaDefintion(true)
        .includeDirectives(false)
)

fun GraphQLSchema.print(): String {
    val string = schemaPrinter.print(this)
    val directivesToString = this.directives.joinToString("\n") { directive -> """
        #${directive.description}
        directive @${directive.name} on ${directive.validLocations().joinToString(" | ") { loc -> loc.name }}
        
    """.trimIndent()
    }
    return string + "\n" + directivesToString
}
