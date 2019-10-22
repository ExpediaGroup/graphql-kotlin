/*
 * Copyright 2019 Expedia, Inc
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
import io.github.classgraph.ClassGraph
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.jvmName

/**
 * Generates federated GraphQL schemas based on the specified configuration.
 */
open class FederatedSchemaGenerator(generatorConfig: FederatedSchemaGeneratorConfig) : SchemaGenerator(generatorConfig) {

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
    @Suppress("Detekt.SpreadOperator")
    fun GraphQLSchema.Builder.federation(supportedPackages: List<String>): GraphQLSchema.Builder {
        val scanResult = ClassGraph().enableAllInfo().whitelistPackages(*supportedPackages.toTypedArray()).scan()

        scanResult.getClassesWithAnnotation(ExtendsDirective::class.jvmName)
            .map { it.loadClass().kotlin }
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
