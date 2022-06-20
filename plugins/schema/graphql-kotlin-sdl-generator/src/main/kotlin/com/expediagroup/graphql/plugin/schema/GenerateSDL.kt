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

package com.expediagroup.graphql.plugin.schema

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import com.expediagroup.graphql.generator.hooks.NoopSchemaGeneratorHooks
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider
import com.expediagroup.graphql.server.Schema
import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.operations.Subscription
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.ServiceLoader

private val logger: Logger = LoggerFactory.getLogger("generateSDL")

/**
 * Generates GraphQL schema in SDL format.
 *
 * Relies on a `ServiceLoader` mechanism to dynamically load an instance of a `SchemaGeneratorHooksProvider` from the classpath.
 */
fun generateSDL(supportedPackages: List<String>): String {
    val hooksProviders = ServiceLoader.load(SchemaGeneratorHooksProvider::class.java).toList()
    val hooks = when {
        hooksProviders.isEmpty() -> {
            logger.warn("No SchemaGeneratorHooksProvider were found, defaulting to NoopSchemaGeneratorHooks")
            NoopSchemaGeneratorHooks
        }
        hooksProviders.size > 1 -> {
            throw RuntimeException("Cannot generate SDL as multiple SchemaGeneratorHooksProviders were found on the classpath")
        }
        else -> {
            val provider = hooksProviders.first()
            logger.debug("SchemaGeneratorHooksProvider found, ${provider.javaClass.simpleName} will be used to generate the hooks")
            provider.hooks()
        }
    }
    val scanResult = ClassGraph()
        .enableAllInfo()
        .acceptPackages(*supportedPackages.toTypedArray())
        .scan()

    val queries = findTopLevelObjects(scanResult, Query::class.java)
    val mutations = findTopLevelObjects(scanResult, Mutation::class.java)
    val subscriptions = findTopLevelObjects(scanResult, Subscription::class.java)
    val schemaObject = findTopLevelObjects(scanResult, Schema::class.java).firstOrNull()

    // TODO support top level name overrides?
    val schema = if (hooks is FederatedSchemaGeneratorHooks) {
        logger.debug("Generating federated schema using hooks = ${hooks.javaClass.simpleName}")
        logger.debug("  query classes = ${queries.map { it.kClass }}")
        logger.debug("  mutation classes = ${mutations.map { it.kClass }}")
        logger.debug("  subscription classes = ${subscriptions.map { it.kClass }}")
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = supportedPackages,
            hooks = hooks
        )
        toFederatedSchema(
            config = config,
            queries = queries,
            mutations = mutations,
            subscriptions = subscriptions,
            schemaObject = schemaObject
        )
    } else {
        logger.debug("Generating schema using hooks = ${hooks.javaClass.simpleName}")
        logger.debug("  query classes = ${queries.map { it.kClass }}")
        logger.debug("  mutation classes = ${mutations.map { it.kClass }}")
        logger.debug("  subscription classes = ${subscriptions.map { it.kClass }}")
        val config = SchemaGeneratorConfig(
            supportedPackages = supportedPackages,
            hooks = hooks
        )
        toSchema(
            config = config,
            queries = queries,
            mutations = mutations,
            subscriptions = subscriptions,
            schemaObject = schemaObject
        )
    }

    scanResult.close()
    return schema.print()
}

private fun findTopLevelObjects(scanResult: ScanResult, markupClass: Class<*>): List<TopLevelObject> =
    scanResult.getClassesImplementing(markupClass.name)
        .map { it.loadClass() }
        .map { TopLevelObject(null, it.kotlin) }
