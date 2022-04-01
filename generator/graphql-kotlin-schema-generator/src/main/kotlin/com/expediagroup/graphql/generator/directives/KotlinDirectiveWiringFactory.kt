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

package com.expediagroup.graphql.generator.directives

import com.expediagroup.graphql.generator.exceptions.InvalidSchemaDirectiveWiringException
import com.expediagroup.graphql.generator.internal.extensions.getAllAppliedDirectives
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLSchemaElement

/**
 * Wiring factory that is used to provide the directives.
 */
open class KotlinDirectiveWiringFactory(
    private val manualWiring: Map<String, KotlinSchemaDirectiveWiring> = emptyMap()
) {

    /**
     * Wire up the directive based on the GraphQL type.
     */
    fun onWire(graphQLSchemaElement: GraphQLSchemaElement, coordinates: FieldCoordinates? = null, codeRegistry: GraphQLCodeRegistry.Builder): GraphQLSchemaElement {
        if (graphQLSchemaElement !is GraphQLDirectiveContainer) return graphQLSchemaElement

        return wireDirectives(graphQLSchemaElement, coordinates, graphQLSchemaElement.getAllAppliedDirectives(), codeRegistry)
    }

    /**
     * Retrieve schema directive wiring for the specified environment or NULL if wiring is not supported by this factory.
     */
    open fun getSchemaDirectiveWiring(environment: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): KotlinSchemaDirectiveWiring? = null

    @Suppress("Detekt.ThrowsCount")
    private fun wireDirectives(
        element: GraphQLDirectiveContainer,
        coordinates: FieldCoordinates?,
        directives: List<GraphQLAppliedDirective>,
        codeRegistry: GraphQLCodeRegistry.Builder
    ): GraphQLDirectiveContainer {
        var modifiedObject = element
        for (directive in directives) {
            val env = if (modifiedObject is GraphQLFieldDefinition) {
                KotlinFieldDirectiveEnvironment(
                    field = modifiedObject,
                    fieldDirective = directive,
                    coordinates = coordinates ?: throw InvalidSchemaDirectiveWiringException("Unable to wire directive on a field due to missing field coordinates"),
                    codeRegistry = codeRegistry
                )
            } else {
                KotlinSchemaDirectiveEnvironment(
                    element = modifiedObject,
                    directive = directive,
                    codeRegistry = codeRegistry
                )
            }

            val directiveWiring = discoverWiringProvider(directive.name, env)
            if (directiveWiring != null) {
                modifiedObject = directiveWiring.wireOnEnvironment(env)
            }
        }
        return modifiedObject
    }

    private fun discoverWiringProvider(directiveName: String, env: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): KotlinSchemaDirectiveWiring? =
        if (directiveName in manualWiring) {
            manualWiring[directiveName]
        } else {
            getSchemaDirectiveWiring(env)
        }
}
