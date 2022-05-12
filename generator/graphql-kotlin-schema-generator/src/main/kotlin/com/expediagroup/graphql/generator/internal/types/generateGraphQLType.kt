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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.extensions.unwrapType
import com.expediagroup.graphql.generator.internal.extensions.getCustomTypeAnnotation
import com.expediagroup.graphql.generator.internal.extensions.getCustomUnionClassWithMetaUnionAnnotation
import com.expediagroup.graphql.generator.internal.extensions.getKClass
import com.expediagroup.graphql.generator.internal.extensions.getMetaUnionAnnotation
import com.expediagroup.graphql.generator.internal.extensions.getUnionAnnotation
import com.expediagroup.graphql.generator.internal.extensions.isAnnotation
import com.expediagroup.graphql.generator.internal.extensions.isEnum
import com.expediagroup.graphql.generator.internal.extensions.isInterface
import com.expediagroup.graphql.generator.internal.extensions.isListType
import com.expediagroup.graphql.generator.internal.extensions.isUnion
import com.expediagroup.graphql.generator.internal.extensions.wrapInNonNull
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Return a basic GraphQL type given all the information about the kotlin type.
 */
internal fun generateGraphQLType(generator: SchemaGenerator, type: KType, typeInfo: GraphQLKTypeMetadata = GraphQLKTypeMetadata()): GraphQLType {
    val hookGraphQLType = generator.config.hooks.willGenerateGraphQLType(type)
    val graphQLType = hookGraphQLType
        ?: generateScalar(generator, type)
        ?: objectFromReflection(generator, type, typeInfo)

    // Do not call the hook on GraphQLTypeReference as we have not generated the type yet
    val unwrappedType = graphQLType.unwrapType()
    val typeWithNullability = graphQLType.wrapInNonNull(type)
    if (unwrappedType !is GraphQLTypeReference) {
        return generator.config.hooks.didGenerateGraphQLType(type, typeWithNullability)
    }

    return typeWithNullability
}

private fun objectFromReflection(generator: SchemaGenerator, type: KType, typeInfo: GraphQLKTypeMetadata): GraphQLType {
    val cachedType = generator.cache.get(type, typeInfo)

    if (cachedType != null) {
        return cachedType
    }

    val kClass = type.getKClass()

    return generator.cache.buildIfNotUnderConstruction(kClass, typeInfo) {
        val graphQLType = getGraphQLType(generator, kClass, type, typeInfo)

        /*
         * For a field using the meta union annotation, the `type` is `Any`, but we need to pass the annotation with the meta union annotation as the type
         * since that is really the type generated from reflection and has any potential directives on it needed by the hook
         */
        val metaUnion = typeInfo.fieldAnnotations.firstOrNull { it.getMetaUnionAnnotation() != null }
        val resolvedType = if (kClass.isInstance(Any::class) && metaUnion != null) {
            metaUnion.annotationClass.createType()
        } else {
            type
        }

        generator.config.hooks.willAddGraphQLTypeToSchema(resolvedType, graphQLType)
    }
}

private fun getGraphQLType(
    generator: SchemaGenerator,
    kClass: KClass<*>,
    type: KType,
    typeInfo: GraphQLKTypeMetadata
): GraphQLType {
    val customTypeAnnotation = typeInfo.fieldAnnotations.getCustomTypeAnnotation()
    if (customTypeAnnotation != null) {
        return GraphQLTypeReference.typeRef(customTypeAnnotation.typeName)
    }

    return when {
        kClass.isEnum() -> @Suppress("UNCHECKED_CAST") (generateEnum(generator, kClass as KClass<Enum<*>>))
        kClass.isListType(typeInfo.isDirective) -> generateList(generator, type, typeInfo)
        kClass.isUnion(typeInfo.fieldAnnotations) -> generateUnion(
            generator,
            kClass,
            typeInfo.fieldAnnotations.getUnionAnnotation(),
            if (kClass.isAnnotation()) kClass else typeInfo.fieldAnnotations.getCustomUnionClassWithMetaUnionAnnotation()
        )
        kClass.isInterface() -> generateInterface(generator, kClass)
        typeInfo.inputType -> generateInputObject(generator, kClass)
        else -> generateObject(generator, kClass)
    }
}
