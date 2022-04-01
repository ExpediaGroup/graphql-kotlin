/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.extensions

import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import java.util.function.Predicate

/**
 * Prints out SDL representation of a target schema.
 *
 * @param includeIntrospectionTypes boolean flag indicating whether SDL should include introspection types
 * @param includeScalarTypes boolean flag indicating whether SDL should include custom schema scalars
 * @param includeDefaultSchemaDefinition boolean flag indicating whether SDL should include schema definition if using
 *   default root type names
 * @param includeDirectives boolean flag indicating whether SDL should include directive information
 * @param includeDirectivesFilter Predicate to filter out specific directives. Defaults to filter all directives by the value of [includeDirectives]
 * @param includeDirectiveDefinitions Include the definitions of directives at the top of the schema
 */
fun GraphQLSchema.print(
    includeIntrospectionTypes: Boolean = false,
    includeScalarTypes: Boolean = true,
    includeDefaultSchemaDefinition: Boolean = true,
    includeDirectives: Boolean = true,
    includeDirectivesFilter: Predicate<String> = Predicate { includeDirectives },
    includeDirectiveDefinitions: Boolean = true
): String {
    val schemaPrinter = SchemaPrinter(
        SchemaPrinter.Options.defaultOptions()
            .includeIntrospectionTypes(includeIntrospectionTypes)
            .includeScalarTypes(includeScalarTypes)
            .includeSchemaDefinition(includeDefaultSchemaDefinition)
            .includeDirectives(includeDirectives)
            .includeDirectives(includeDirectivesFilter)
            .includeDirectiveDefinitions(includeDirectiveDefinitions)
    )
    return schemaPrinter.print(this)
}
