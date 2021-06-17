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
import com.expediagroup.graphql.generator.annotations.GraphQLAbstractType
import com.expediagroup.graphql.generator.extensions.unwrapType
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.internal.extensions.isAny
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import graphql.GraphQLException
import graphql.TypeResolutionEnvironment
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

internal fun generateUnion(generator: SchemaGenerator, kClass: KClass<*>, abstractTypeAnnotation: GraphQLAbstractType?): GraphQLUnionType {
    return if (abstractTypeAnnotation != null) {
        generateUnionFromAbstractTypeAnnotation(generator, kClass, abstractTypeAnnotation)
    } else {
        generateUnionFromKClass(generator, kClass)
    }
}

private fun generateUnionFromAbstractTypeAnnotation(generator: SchemaGenerator, kClass: KClass<*>, abstractTypeAnnotation: GraphQLAbstractType): GraphQLUnionType {
    val builder = GraphQLUnionType.newUnionType()
    builder.name(abstractTypeAnnotation.name)

    val possibleTypes = abstractTypeAnnotation.possibleTypes.toList()
    val types = if (kClass.isAny() || (possibleTypes.isNotEmpty() && generator.classScanner.getSubTypesOf(kClass).containsAll(possibleTypes))) {
        possibleTypes
    } else {
        throw GraphQLException("The specified possible types are not all subtypes of ${kClass.qualifiedName}") // TODO: throw correct exception
    }

    return createUnion(generator, builder, types)
}

private fun generateUnionFromKClass(generator: SchemaGenerator, kClass: KClass<*>): GraphQLUnionType {
    val builder = GraphQLUnionType.newUnionType()
    builder.name(kClass.getSimpleName())
    builder.description(kClass.getGraphQLDescription())

    generateDirectives(generator, kClass, DirectiveLocation.UNION).forEach {
        builder.withDirective(it)
    }

    val types = generator.classScanner.getSubTypesOf(kClass)

    return createUnion(generator, builder, types)
}

private fun createUnion(generator: SchemaGenerator, builder: GraphQLUnionType.Builder, types: List<KClass<*>>): GraphQLUnionType {
    types.map { generateGraphQLType(generator, it.createType()) }
        .forEach {
            when (val unwrappedType = it.unwrapType()) {
                is GraphQLTypeReference -> builder.possibleType(unwrappedType)
                is GraphQLObjectType -> builder.possibleType(unwrappedType)
            }
        }

    val unionType = builder.build()
    generator.codeRegistry.typeResolver(unionType) { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.kotlin.getSimpleName()) }
    return generator.config.hooks.onRewireGraphQLType(unionType).safeCast()
}
