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

package com.expediagroup.graphql.federation

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.generator.SchemaGenerator
import graphql.schema.GraphQLSchema
import org.reflections.Reflections
import kotlin.reflect.full.createType

/**
 * Generates federated GraphQL schemas based on the specified configuration.
 */
class FederatedSchemaGenerator(generatorConfig: FederatedSchemaGeneratorConfig) : SchemaGenerator(generatorConfig) {

    override fun generate(
        queries: List<TopLevelObject>,
        mutations: List<TopLevelObject>,
        subscriptions: List<TopLevelObject>,
        builder: GraphQLSchema.Builder
    ): GraphQLSchema {
        builder.federation(config.supportedPackages)
        return super.generate(queries, mutations, subscriptions, builder)
    }

    /**
     * Scans specified packages for all the federated (extended) types and adds them to the target schema.
     */
    fun GraphQLSchema.Builder.federation(supportedPackages: List<String>): GraphQLSchema.Builder {
        supportedPackages
            .map { pkg -> Reflections(pkg).getTypesAnnotatedWith(ExtendsDirective::class.java).map { it.kotlin } }
            .flatten()
            .map {
                val graphQLType = if (it.isAbstract) {
                    interfaceType(it)
                } else {
                    objectType(it)
                }

                // workaround to explicitly apply validation
                config.hooks.didGenerateGraphQLType(it.createType(), graphQLType)
            }
            .forEach {
                this.additionalType(it)
            }
        return this
    }
}
