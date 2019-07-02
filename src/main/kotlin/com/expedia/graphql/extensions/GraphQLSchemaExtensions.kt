package com.expedia.graphql.extensions

import com.expedia.graphql.directives.DeprecatedDirective
import graphql.Directives
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter

/**
 * Prints out SDL representation of a target schema.
 *
 * @param includeIntrospectionTypes boolean flag indicating whether SDL should include introspection types
 * @param includeScalarTypes boolean flag indicating whether SDL should include custom schema scalars
 * @param includeExtendedScalarTypes boolean flag indicating whether SDL should include extended scalars (e.g. Long)
 *   supported by graphql-java, if set will automatically also set the includeScalarTypes flag
 * @param includeDefaultSchemaDefinition boolean flag indicating whether SDL should include schema definition if using
 *   default root type names
 * @param includeDirectives boolean flag indicating whether SDL should include directive information
 */
fun GraphQLSchema.print(
    includeIntrospectionTypes: Boolean = false,
    includeScalarTypes: Boolean = true,
    includeExtendedScalarTypes: Boolean = true,
    includeDefaultSchemaDefinition: Boolean = true,
    includeDirectives: Boolean = true
): String {
    val schemaPrinter = SchemaPrinter(
        SchemaPrinter.Options.defaultOptions()
            .includeIntrospectionTypes(includeIntrospectionTypes)
            .includeScalarTypes(includeScalarTypes || includeExtendedScalarTypes)
            .includeExtendedScalarTypes(includeExtendedScalarTypes)
            .includeSchemaDefintion(includeDefaultSchemaDefinition)
            .includeDirectives(includeDirectives)
    )

    var schemaString = schemaPrinter.print(this)
    if (includeDirectives) {
        // graphql-java SchemaPrinter filters out common directives, below is a workaround to print default built-in directives
        val defaultDirectives = arrayOf(Directives.IncludeDirective, Directives.SkipDirective, DeprecatedDirective)
        val directivesToString = defaultDirectives.joinToString("\n\n") { directive -> """
                #${directive.description}
                directive @${directive.name} on ${directive.validLocations().joinToString(" | ") { loc -> loc.name }}
            """.trimIndent()
        }
        schemaString += "\n" + directivesToString
    }
    return schemaString
}
