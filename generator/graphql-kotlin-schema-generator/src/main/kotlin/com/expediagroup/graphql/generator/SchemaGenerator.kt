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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.generator.exceptions.InvalidPackagesException
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.internal.extensions.getKClass
import com.expediagroup.graphql.generator.internal.state.AdditionalType
import com.expediagroup.graphql.generator.internal.state.ClassScanner
import com.expediagroup.graphql.generator.internal.state.TypesCache
import com.expediagroup.graphql.generator.internal.types.GraphQLKTypeMetadata
import com.expediagroup.graphql.generator.internal.types.generateGraphQLType
import com.expediagroup.graphql.generator.internal.types.generateMutations
import com.expediagroup.graphql.generator.internal.types.generateQueries
import com.expediagroup.graphql.generator.internal.types.generateSchemaDirectives
import com.expediagroup.graphql.generator.internal.types.generateSubscriptions
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil
import graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Generate a schema object given some configuration and top level objects for the queries, mutations, and subscriptions.
 *
 * This class maintains the state of the schema while generation is taking place. It is passed into the internal functions,
 * so they can use the cache and add additional types and directives into the schema as they parse the Kotlin code.
 *
 * This class should be used from a try-with-resources block
 * or another closable object as the internals can take up a lot of resources.
 */
open class SchemaGenerator(internal val config: SchemaGeneratorConfig) : Closeable {

    internal val additionalTypes: MutableSet<AdditionalType> = mutableSetOf()
    internal val classScanner = ClassScanner(config.supportedPackages)
    internal val cache = TypesCache(config.supportedPackages)
    internal val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
    internal val directives = ConcurrentHashMap<String, GraphQLDirective>()

    /**
     * Validate that the supported packages contain classes
     */
    init {
        if (classScanner.isEmptyScan()) {
            throw InvalidPackagesException(config.supportedPackages)
        }
    }

    /**
     * Generate a schema given a list of objects to parse for the queries, mutations, and subscriptions.
     */
    open fun generateSchema(
        queries: List<TopLevelObject>,
        mutations: List<TopLevelObject> = emptyList(),
        subscriptions: List<TopLevelObject> = emptyList(),
        additionalTypes: Set<KType> = emptySet(),
        additionalInputTypes: Set<KType> = emptySet(),
        schemaObject: TopLevelObject? = null
    ): GraphQLSchema {

        this.additionalTypes.addAll(additionalTypes.map { AdditionalType(it, inputType = false) })
        this.additionalTypes.addAll(additionalInputTypes.map { AdditionalType(it, inputType = true) })

        if (!config.introspectionEnabled) {
            codeRegistry.fieldVisibility(NoIntrospectionGraphqlFieldVisibility.NO_INTROSPECTION_FIELD_VISIBILITY)
        }

        return GraphQLSchema.newSchema()
            .query(generateQueries(this, queries))
            .mutation(generateMutations(this, mutations))
            .subscription(generateSubscriptions(this, subscriptions))
            .additionalTypes(generateAdditionalTypes())
            .additionalDirectives(directives.values.toSet())
            .codeRegistry(codeRegistry.build())
            .also { builder ->
                if (schemaObject != null) {
                    directives.clear()

                    builder.description(schemaObject.kClass.getGraphQLDescription())
                        .withSchemaAppliedDirectives(generateSchemaDirectives(this, schemaObject))
                        .additionalDirectives(directives.values.toSet())
                }
            }
            .run {
                config.hooks.willBuildSchema(this)
            }.build()
    }

    /**
     * Add all types with the following annotation to the schema.
     *
     * This is helpful for things like federation or combining external schemas.
     */
    protected fun addAdditionalTypesWithAnnotation(annotation: KClass<*>, inputType: Boolean = false) {
        classScanner.getClassesWithAnnotation(annotation).forEach { kClass ->
            if (config.hooks.isValidAdditionalType(kClass, inputType)) {
                additionalTypes.add(AdditionalType(kClass.createType(), inputType))
            }
        }
    }

    /**
     * Generate the GraphQL type for all the `additionalTypes`.
     *
     * If you need to provide more custom additional types that were not picked up from reflection of the schema objects,
     * you can provide more types to be added through [generateSchema] or the config.
     *
     * This function loops because while generating the additionalTypes it is possible to create more additional types that need to be processed.
     */
    protected fun generateAdditionalTypes(): Set<GraphQLType> {
        val graphqlTypes = this.config.additionalTypes.toMutableSet()
        while (this.additionalTypes.isNotEmpty()) {
            val currentlyProcessedTypes = LinkedHashSet(this.additionalTypes)
            this.additionalTypes.clear()
            graphqlTypes.addAll(
                currentlyProcessedTypes.map {
                    GraphQLTypeUtil.unwrapNonNull(generateGraphQLType(this, it.kType, GraphQLKTypeMetadata(inputType = it.inputType, fieldAnnotations = it.kType.getKClass().annotations)))
                }
            )
        }

        return graphqlTypes
    }

    /**
     * Clear the generator type cache, reflection scan, additional types,
     * and the saved directives. You may want call this after you have
     * called [generateSchema] and performed some other actions which is why
     * we have a separate method to explicitly clear.
     *
     * If you use the built in [com.expediagroup.graphql.generator.toSchema], we will handle
     * clean up of resources for you.
     */
    override fun close() {
        classScanner.close()
        cache.close()
        additionalTypes.clear()
        directives.clear()
    }
}
