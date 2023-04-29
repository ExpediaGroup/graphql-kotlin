/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.plugin.graalvm

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import com.expediagroup.graphql.generator.hooks.NoopSchemaGeneratorHooks
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.plugin.graalvm.DefaultMetadataLoader.loadDefaultReflectMetadata
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider
import com.expediagroup.graphql.server.Schema
import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.operations.Subscription
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.ServiceLoader

private val logger: Logger = LoggerFactory.getLogger("generateGraalVmMetadata")
private val objectMapper: ObjectMapper = jacksonObjectMapper()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)

/**
 * Generate GraalVM reflect metadata for the underlying GraphQL schema.
 */
fun generateGraalVmMetadata(targetDirectory: File, supportedPackages: List<String>, mainClassName: String? = null) {
    if (mainClassName != null) {
        val nativeImageConfiguration = generateNativeImageConfiguration(mainClassName)
        File(targetDirectory, "native-image.properties").writeText(nativeImageConfiguration)
    }

    val reflectMetadata: List<ClassMetadata> = generateGraalVmReflectMetadata(supportedPackages) + loadDefaultReflectMetadata()
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(File(targetDirectory, "reflect-config.json"), reflectMetadata)

    val resourceConfigMetadata = DefaultMetadataLoader.defaultResourceMetadataStream()
    resourceConfigMetadata.use { resourceConfigStream ->
        Files.copy(resourceConfigStream, targetDirectory.toPath().resolve("resource-config.json"), StandardCopyOption.REPLACE_EXISTING)
    }
}

/**
 * Generate GraalVM reflect metadata for the underlying GraphQL schema.
 */
fun generateGraalVmReflectMetadata(supportedPackages: List<String>): List<ClassMetadata> {
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

    val result = scanResult.use {
        val rootObjects: TopLevelObjects = findTopLevelObjects(scanResult, supportedPackages)
        val dataFetcherFactoryProvider = MetadataCapturingDataFetcherFactoryProvider(scanResult, supportedPackages)
        val typeResolver = MetadataCapturingGraphQLTypeResolver(supportedPackages)

        if (hooks is FederatedSchemaGeneratorHooks) {
            logger.debug("Generating federated schema using hooks = ${hooks.javaClass.simpleName}")
            logger.debug("  query classes = ${rootObjects.queries.map { it.kClass }}")
            logger.debug("  mutation classes = ${rootObjects.mutations.map { it.kClass }}")
            logger.debug("  subscription classes = ${rootObjects.subscriptions.map { it.kClass }}")
            val config = FederatedSchemaGeneratorConfig(
                supportedPackages = supportedPackages,
                hooks = hooks,
                dataFetcherFactoryProvider = dataFetcherFactoryProvider,
                typeResolver = typeResolver
            )
            toFederatedSchema(
                config = config,
                queries = rootObjects.queries,
                mutations = rootObjects.mutations,
                subscriptions = rootObjects.subscriptions,
                schemaObject = rootObjects.schemaObject
            )
        } else {
            logger.debug("Generating schema using hooks = ${hooks.javaClass.simpleName}")
            logger.debug("  query classes = ${rootObjects.queries.map { it.kClass }}")
            logger.debug("  mutation classes = ${rootObjects.mutations.map { it.kClass }}")
            logger.debug("  subscription classes = ${rootObjects.subscriptions.map { it.kClass }}")
            val config = SchemaGeneratorConfig(
                supportedPackages = supportedPackages,
                hooks = hooks,
                dataFetcherFactoryProvider = dataFetcherFactoryProvider,
                typeResolver = typeResolver
            )
            toSchema(
                config = config,
                queries = rootObjects.queries,
                mutations = rootObjects.mutations,
                subscriptions = rootObjects.subscriptions,
                schemaObject = rootObjects.schemaObject
            )
        }

        typeResolver.close()
        dataFetcherFactoryProvider.reflectMetadata() + typeResolver.supertypes.map { ClassMetadata(name = it) }
    }
    return result.sortedBy { it.name }
}

private fun findTopLevelObjects(scanResult: ScanResult, supportedPackages: List<String>): TopLevelObjects {
    val queries = findTopLevelObjects(scanResult, Query::class.java)
    val mutations = findTopLevelObjects(scanResult, Mutation::class.java)
    val subscriptions = findTopLevelObjects(scanResult, Subscription::class.java)
    val schemaObject = findTopLevelObjects(scanResult, Schema::class.java).firstOrNull()
    return TopLevelObjects(queries, mutations, subscriptions, schemaObject)
}

private fun findTopLevelObjects(scanResult: ScanResult, markupClass: Class<*>): List<TopLevelObject> =
    scanResult.getClassesImplementing(markupClass.name)
        .map { it.loadClass() }
        .map { TopLevelObject(null, it.kotlin) }
