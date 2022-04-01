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

import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLFieldDefinition

/**
 * KotlinFieldDirectiveEnvironment holds wiring information for applying directives on GraphQL fields.
 */
class KotlinFieldDirectiveEnvironment(
    field: GraphQLFieldDefinition,
    fieldDirective: GraphQLAppliedDirective,
    private val coordinates: FieldCoordinates,
    codeRegistry: GraphQLCodeRegistry.Builder
) : KotlinSchemaDirectiveEnvironment<GraphQLFieldDefinition>(element = field, directive = fieldDirective, codeRegistry = codeRegistry) {

    /**
     * Retrieve current data fetcher associated with the target element.
     */
    fun getDataFetcher(): DataFetcher<*> = codeRegistry.getDataFetcher(coordinates, element)

    /**
     * Update target element data fetcher.
     */
    fun setDataFetcher(newDataFetcher: DataFetcher<Any>) {
        codeRegistry.dataFetcher(coordinates, newDataFetcher)
    }
}
