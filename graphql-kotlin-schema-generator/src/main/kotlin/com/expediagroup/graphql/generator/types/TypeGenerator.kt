/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.exceptions.InvalidPackagesException
import com.expediagroup.graphql.extensions.unwrapType
import com.expediagroup.graphql.generator.extensions.getKClass
import com.expediagroup.graphql.generator.extensions.isEnum
import com.expediagroup.graphql.generator.extensions.isInterface
import com.expediagroup.graphql.generator.extensions.isListType
import com.expediagroup.graphql.generator.extensions.isUnion
import com.expediagroup.graphql.generator.extensions.wrapInNonNull
import com.expediagroup.graphql.generator.state.AdditionalType
import com.expediagroup.graphql.generator.state.ClassScanner
import com.expediagroup.graphql.generator.state.TypesCache
import com.expediagroup.graphql.generator.state.TypesCacheKey
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Generator that takes in a specific [KType] and returns the appropiate [GraphQLType].
 *
 * Keeps and internal state of previously seen types, additional types from interfaces,
 * and directives that need to be added to a schema after all the types are generated.
 */
class TypeGenerator(internal val config: SchemaGeneratorConfig) : Closeable {

    internal val additionalTypes: MutableSet<AdditionalType> = mutableSetOf()
    internal val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
    internal val directives = ConcurrentHashMap<String, GraphQLDirective>()
    internal val classScanner = ClassScanner(config.supportedPackages)
    internal val cache = TypesCache(config.supportedPackages)

    /**
     * Validate that the supported packages contain classes
     */
    init {
        if (classScanner.isEmptyScan()) {
            throw InvalidPackagesException(config.supportedPackages)
        }
    }

    /**
     * Return a basic GraphQL type given all the information about the kotlin type.
     */
    fun generateGraphQLType(type: KType, inputType: Boolean = false): GraphQLType {
        val hookGraphQLType = config.hooks.willGenerateGraphQLType(type)
        val graphQLType = hookGraphQLType
            ?: generateScalar(config.hooks, type)
            ?: objectFromReflection(type, inputType)

        // Do not call the hook on GraphQLTypeReference as we have not generated the type yet
        val unwrappedType = graphQLType.unwrapType()
        val typeWithNullability = graphQLType.wrapInNonNull(type)
        if (unwrappedType !is GraphQLTypeReference) {
            return config.hooks.didGenerateGraphQLType(type, typeWithNullability)
        }

        return typeWithNullability
    }

    private fun objectFromReflection(type: KType, inputType: Boolean): GraphQLType {
        val cacheKey = TypesCacheKey(type, inputType)
        val cachedType = cache.get(cacheKey)

        if (cachedType != null) {
            return cachedType
        }

        val kClass = type.getKClass()

        return cache.buildIfNotUnderConstruction(kClass, inputType) {
            val graphQLType = getGraphQLType(kClass, inputType, type)
            config.hooks.willAddGraphQLTypeToSchema(type, graphQLType)
        }
    }

    private fun getGraphQLType(kClass: KClass<*>, inputType: Boolean, type: KType): GraphQLType = when {
        kClass.isEnum() -> @Suppress("UNCHECKED_CAST") (generateEnum(this, kClass as KClass<Enum<*>>))
        kClass.isListType() -> generateList(this, type, inputType)
        kClass.isUnion() -> generateUnion(this, kClass)
        kClass.isInterface() -> generateInterface(this, kClass)
        inputType -> generateInputObject(this, kClass)
        else -> generateObject(this, kClass)
    }

    /**
     * Clear the generator type cache and reflection scan once
     * all types have been generated.
     */
    override fun close() {
        classScanner.close()
        cache.close()
        additionalTypes.clear()
        directives.clear()
    }
}
