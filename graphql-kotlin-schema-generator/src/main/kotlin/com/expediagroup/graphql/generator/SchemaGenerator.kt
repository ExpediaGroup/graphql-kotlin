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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.generator.extensions.getKClass
import com.expediagroup.graphql.generator.extensions.isEnum
import com.expediagroup.graphql.generator.extensions.isInterface
import com.expediagroup.graphql.generator.extensions.isListType
import com.expediagroup.graphql.generator.extensions.isUnion
import com.expediagroup.graphql.generator.extensions.wrapInNonNull
import com.expediagroup.graphql.generator.state.SchemaGeneratorState
import com.expediagroup.graphql.generator.state.TypesCacheKey
import com.expediagroup.graphql.generator.types.generateEnum
import com.expediagroup.graphql.generator.types.generateInputObject
import com.expediagroup.graphql.generator.types.generateInterface
import com.expediagroup.graphql.generator.types.generateList
import com.expediagroup.graphql.generator.types.generateMutations
import com.expediagroup.graphql.generator.types.generateObject
import com.expediagroup.graphql.generator.types.generateQueries
import com.expediagroup.graphql.generator.types.generateScalar
import com.expediagroup.graphql.generator.types.generateSubscriptions
import com.expediagroup.graphql.generator.types.generateUnion
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLTypeUtil
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("LeakingThis")
open class SchemaGenerator(val config: SchemaGeneratorConfig) {

    internal val state = SchemaGeneratorState(config.supportedPackages)
    internal val subTypeMapper = SubTypeMapper(config.supportedPackages)
    internal val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()

    open fun generate(
        queries: List<TopLevelObject>,
        mutations: List<TopLevelObject>,
        subscriptions: List<TopLevelObject>,
        builder: GraphQLSchema.Builder = GraphQLSchema.newSchema()
    ): GraphQLSchema {
        builder.query(generateQueries(this, queries))
        builder.mutation(generateMutations(this, mutations))
        builder.subscription(generateSubscriptions(this, subscriptions))

        // add unreferenced interface implementations
        state.additionalTypes.forEach {
            builder.additionalType(it)
        }

        builder.additionalDirectives(state.directives.values.toSet())
        builder.codeRegistry(codeRegistry.build())

        val schema = config.hooks.willBuildSchema(builder).build()

        // Clean up the classpath scanner
        subTypeMapper.close()

        return schema
    }

    open fun graphQLTypeOf(type: KType, inputType: Boolean = false, annotatedAsID: Boolean = false): GraphQLType {
        val hookGraphQLType = config.hooks.willGenerateGraphQLType(type)
        val graphQLType = hookGraphQLType
            ?: generateScalar(this, type, annotatedAsID)
            ?: objectFromReflection(type, inputType)

        // Do not call the hook on GraphQLTypeReference as we have not generated the type yet
        val unwrappedType = GraphQLTypeUtil.unwrapType(graphQLType).lastElement()
        val typeWithNullability = graphQLType.wrapInNonNull(type)
        if (unwrappedType !is GraphQLTypeReference) {
            return config.hooks.didGenerateGraphQLType(type, typeWithNullability)
        }

        return typeWithNullability
    }

    private fun objectFromReflection(type: KType, inputType: Boolean): GraphQLType {
        val cacheKey = TypesCacheKey(type, inputType)
        val cachedType = state.cache.get(cacheKey)

        if (cachedType != null) {
            return cachedType
        }

        val kClass = type.getKClass()
        val graphQLType = state.cache.buildIfNotUnderConstruction(kClass, inputType) { getGraphQLType(kClass, inputType, type) }

        return config.hooks.willAddGraphQLTypeToSchema(type, graphQLType)
    }

    private fun getGraphQLType(kClass: KClass<*>, inputType: Boolean, type: KType): GraphQLType = when {
        kClass.isEnum() -> @Suppress("UNCHECKED_CAST") (generateEnum(this, kClass as KClass<Enum<*>>))
        kClass.isListType() -> generateList(this, type, inputType)
        kClass.isUnion() -> generateUnion(this, kClass)
        kClass.isInterface() -> generateInterface(this, kClass)
        inputType -> generateInputObject(this, kClass)
        else -> generateObject(this, kClass)
    }
}
