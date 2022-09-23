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

package com.expediagroup.graphql.generator.federation

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import graphql.schema.GraphQLSchema
import kotlin.reflect.KType

/**
 * Generates federated GraphQL schemas based on the specified configuration.
 */
open class FederatedSchemaGenerator(generatorConfig: FederatedSchemaGeneratorConfig) : SchemaGenerator(generatorConfig) {

    /**
     * Scans specified packages for all the federated (extended) types and adds them to the schema additional types,
     * then it generates the schema as usual using the [FederatedSchemaGeneratorConfig].
     */
    override fun generateSchema(
        queries: List<TopLevelObject>,
        mutations: List<TopLevelObject>,
        subscriptions: List<TopLevelObject>,
        additionalTypes: Set<KType>,
        additionalInputTypes: Set<KType>,
        schemaObject: TopLevelObject?
    ): GraphQLSchema {
        addAdditionalTypesWithAnnotation(KeyDirective::class, inputType = false)
        return super.generateSchema(queries, mutations, subscriptions, additionalTypes, additionalInputTypes, schemaObject)
    }
}
