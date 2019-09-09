/*
 * Copyright 2019 Expedia Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.extensions

import com.expediagroup.graphql.directives.DeprecatedDirective
import graphql.Directives
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLTypeUtil
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
                directive @${directive.name}${parseDirectiveArguments(directive)} on ${directive.validLocations().joinToString(" | ") { loc -> loc.name }}
            """.trimIndent()
        }
        schemaString += "\n" + directivesToString
    }
    return schemaString
}

private fun parseDirectiveArguments(directive: GraphQLDirective) = if (directive.arguments.isNotEmpty()) {
    """(${directive.arguments.joinToString(", ") { argument ->
        "${argument.name}: ${GraphQLTypeUtil.simplePrint(argument.type)}${parseDirectiveArgumentValue(argument)}" }})"""
} else {
    ""
}

private fun parseDirectiveArgumentValue(argument: GraphQLArgument) = if (null != argument.defaultValue) {
    " = \"${argument.defaultValue}\""
} else {
    ""
}
