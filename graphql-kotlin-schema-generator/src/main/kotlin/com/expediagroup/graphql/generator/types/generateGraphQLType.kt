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

import com.expediagroup.graphql.extensions.unwrapType
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.extensions.getKClass
import com.expediagroup.graphql.generator.extensions.isEnum
import com.expediagroup.graphql.generator.extensions.isInterface
import com.expediagroup.graphql.generator.extensions.isListType
import com.expediagroup.graphql.generator.extensions.isUnion
import com.expediagroup.graphql.generator.extensions.wrapInNonNull
import com.expediagroup.graphql.generator.state.TypesCacheKey
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Return a basic GraphQL type given all the information about the kotlin type.
 */
internal fun generateGraphQLType(generator: SchemaGenerator, type: KType, inputType: Boolean = false, annotatedAsID: Boolean = false): GraphQLType {
    val hookGraphQLType = generator.config.hooks.willGenerateGraphQLType(type)
    val graphQLType = hookGraphQLType
        ?: generateScalar(generator, type, annotatedAsID)
        ?: objectFromReflection(generator, type, inputType)

    // Do not call the hook on GraphQLTypeReference as we have not generated the type yet
    val unwrappedType = graphQLType.unwrapType()
    val typeWithNullability = graphQLType.wrapInNonNull(type)
    if (unwrappedType !is GraphQLTypeReference) {
        return generator.config.hooks.didGenerateGraphQLType(type, typeWithNullability)
    }

    return typeWithNullability
}

private fun objectFromReflection(generator: SchemaGenerator, type: KType, inputType: Boolean): GraphQLType {
    val cacheKey = TypesCacheKey(type, inputType)
    val cachedType = generator.cache.get(cacheKey)

    if (cachedType != null) {
        return cachedType
    }

    val kClass = type.getKClass()

    return generator.cache.buildIfNotUnderConstruction(kClass, inputType) {
        val graphQLType = getGraphQLType(generator, kClass, inputType, type)
        generator.config.hooks.willAddGraphQLTypeToSchema(type, graphQLType)
    }
}

private fun getGraphQLType(generator: SchemaGenerator, kClass: KClass<*>, inputType: Boolean, type: KType): GraphQLType = when {
    kClass.isEnum() -> @Suppress("UNCHECKED_CAST") (generateEnum(generator, kClass as KClass<Enum<*>>))
    kClass.isListType() -> generateList(generator, type, inputType)
    kClass.isUnion() -> generateUnion(generator, kClass)
    kClass.isInterface() -> generateInterface(generator, kClass)
    inputType -> generateInputObject(generator, kClass)
    else -> generateObject(generator, kClass)
}
