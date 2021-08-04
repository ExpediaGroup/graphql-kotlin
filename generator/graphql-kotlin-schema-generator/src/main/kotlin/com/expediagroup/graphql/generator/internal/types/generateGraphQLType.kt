/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.extensions.unwrapType
import com.expediagroup.graphql.generator.internal.extensions.getKClass
import com.expediagroup.graphql.generator.internal.extensions.getUnionAnnotation
import com.expediagroup.graphql.generator.internal.extensions.isEnum
import com.expediagroup.graphql.generator.internal.extensions.isInterface
import com.expediagroup.graphql.generator.internal.extensions.isListType
import com.expediagroup.graphql.generator.internal.extensions.isUnion
import com.expediagroup.graphql.generator.internal.extensions.wrapInNonNull
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Return a basic GraphQL type given all the information about the kotlin type.
 */
internal fun generateGraphQLType(generator: SchemaGenerator, type: KType, inputType: Boolean = false, annotations: List<Annotation> = emptyList()): GraphQLType {
    val hookGraphQLType = generator.config.hooks.willGenerateGraphQLType(type)
    val graphQLType = hookGraphQLType
        ?: generateScalar(generator, type)
        ?: objectFromReflection(generator, type, inputType, annotations)

    // Do not call the hook on GraphQLTypeReference as we have not generated the type yet
    val unwrappedType = graphQLType.unwrapType()
    val typeWithNullability = graphQLType.wrapInNonNull(type)
    if (unwrappedType !is GraphQLTypeReference) {
        return generator.config.hooks.didGenerateGraphQLType(type, typeWithNullability)
    }

    return typeWithNullability
}

private fun objectFromReflection(generator: SchemaGenerator, type: KType, inputType: Boolean, annotations: List<Annotation>): GraphQLType {
    val cachedType = generator.cache.get(type, inputType, annotations)

    if (cachedType != null) {
        return cachedType
    }

    val kClass = type.getKClass()

    return generator.cache.buildIfNotUnderConstruction(kClass, inputType, annotations) {
        val graphQLType = getGraphQLType(generator, kClass, inputType, type, annotations)
        generator.config.hooks.willAddGraphQLTypeToSchema(type, graphQLType)
    }
}

private fun getGraphQLType(generator: SchemaGenerator, kClass: KClass<*>, inputType: Boolean, type: KType, fieldAnnotations: List<Annotation>): GraphQLType = when {
    kClass.isEnum() -> @Suppress("UNCHECKED_CAST") (generateEnum(generator, kClass as KClass<Enum<*>>))
    kClass.isListType() -> generateList(generator, type, inputType, fieldAnnotations)
    kClass.isUnion(fieldAnnotations) -> generateUnion(generator, kClass, fieldAnnotations.getUnionAnnotation())
    kClass.isInterface() -> generateInterface(generator, kClass)
    inputType -> generateInputObject(generator, kClass)
    else -> generateObject(generator, kClass)
}
