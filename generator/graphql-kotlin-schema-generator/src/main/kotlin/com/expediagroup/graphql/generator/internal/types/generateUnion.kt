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
import com.expediagroup.graphql.generator.annotations.GraphQLUnion
import com.expediagroup.graphql.generator.exceptions.InvalidUnionException
import com.expediagroup.graphql.generator.extensions.unwrapType
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.internal.types.utils.validateGraphQLName
import graphql.TypeResolutionEnvironment
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

internal fun generateUnion(generator: SchemaGenerator, kClass: KClass<*>, unionAnnotation: GraphQLUnion? = null, customUnionAnnotationClass: KClass<*>? = null): GraphQLUnionType {
    return if (unionAnnotation != null) {
        generateUnionFromAnnotation(generator, unionAnnotation, kClass, customUnionAnnotationClass)
    } else {
        generateUnionFromKClass(generator, kClass)
    }
}

private fun generateUnionFromAnnotation(generator: SchemaGenerator, unionAnnotation: GraphQLUnion, kClass: KClass<*>, customUnionAnnotationClass: KClass<*>?): GraphQLUnionType {
    val unionName = unionAnnotation.name
    validateGraphQLName(unionName, kClass)

    val builder = GraphQLUnionType.newUnionType()
    builder.name(unionName)
    builder.description(unionAnnotation.description)

    customUnionAnnotationClass?.let {
        generateDirectives(generator, customUnionAnnotationClass, DirectiveLocation.UNION).forEach {
            builder.withAppliedDirective(it)
        }
    }

    val possibleTypes = unionAnnotation.possibleTypes.toList()

    return createUnion(unionName, generator, builder, possibleTypes)
}

private fun generateUnionFromKClass(generator: SchemaGenerator, kClass: KClass<*>): GraphQLUnionType {
    val builder = GraphQLUnionType.newUnionType()
    val name = kClass.getSimpleName()
    validateGraphQLName(name, kClass)

    builder.name(name)
    builder.description(kClass.getGraphQLDescription())

    generateDirectives(generator, kClass, DirectiveLocation.UNION).forEach {
        builder.withAppliedDirective(it)
    }

    val types = generator.classScanner.getSubTypesOf(kClass)

    return createUnion(name, generator, builder, types)
}

private fun createUnion(typeName: String, generator: SchemaGenerator, builder: GraphQLUnionType.Builder, types: List<KClass<*>>): GraphQLUnionType {
    if (types.isEmpty()) {
        throw InvalidUnionException(typeName)
    }

    types.map { generateGraphQLType(generator, it.createType()) }
        .forEach {
            when (val unwrappedType = it.unwrapType()) {
                is GraphQLTypeReference -> builder.possibleType(unwrappedType)
                is GraphQLObjectType -> builder.possibleType(unwrappedType)
            }
        }

    val unionType: GraphQLUnionType = builder.build()
    generator.codeRegistry.typeResolver(unionType) { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.kotlin.getSimpleName()) }
    return generator.config.hooks.onRewireGraphQLType(unionType, null, generator.codeRegistry).safeCast()
}
