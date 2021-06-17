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
import com.expediagroup.graphql.generator.internal.extensions.getValidFunctions
import com.expediagroup.graphql.generator.internal.extensions.getValidProperties
import com.expediagroup.graphql.generator.internal.extensions.getValidSuperclasses
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.internal.state.AdditionalType
import graphql.GraphQLException
import graphql.TypeResolutionEnvironment
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

internal fun generateInterface(generator: SchemaGenerator, kClass: KClass<*>, abstractTypeAnnotation: GraphQLAbstractType?): GraphQLInterfaceType {
    return if (abstractTypeAnnotation != null) {
        generateInterfaceFromAbstractTypeAnnotation(generator, kClass, abstractTypeAnnotation)
    } else {
        generateFromKClass(generator, kClass)
    }
}

private fun generateInterfaceFromAbstractTypeAnnotation(generator: SchemaGenerator, kClass: KClass<*>, abstractTypeAnnotation: GraphQLAbstractType): GraphQLInterfaceType {
    val builder = GraphQLInterfaceType.newInterface()
    builder.name(abstractTypeAnnotation.name)
    builder.description(abstractTypeAnnotation.description)

    val interfaceSubTypes = generator.classScanner.getSubTypesOf(kClass).filter { generator.config.hooks.isValidAdditionalType(it, inputType = false) }
    val possibleTypes = abstractTypeAnnotation.possibleTypes.toList()
    val subTypes = if (possibleTypes.isNotEmpty() && interfaceSubTypes.containsAll(possibleTypes)) {
        possibleTypes
    } else {
        throw GraphQLException("The specified possible types are not all valid sub types of ${kClass.qualifiedName}") // TODO: throw correct excpetion
    }

    return createInterface(generator, kClass, builder, subTypes)

}

private fun generateFromKClass(generator: SchemaGenerator, kClass: KClass<*>): GraphQLInterfaceType {
    val builder = GraphQLInterfaceType.newInterface()
    builder.name(kClass.getSimpleName())
    builder.description(kClass.getGraphQLDescription())

    generateDirectives(generator, kClass, DirectiveLocation.INTERFACE).forEach {
        builder.withDirective(it)
    }

    val subTypes = generator.classScanner.getSubTypesOf(kClass).filter { generator.config.hooks.isValidAdditionalType(it, inputType = false) }

    return createInterface(generator, kClass, builder, subTypes)
}

private fun createInterface(generator: SchemaGenerator, kClass: KClass<*>, builder: GraphQLInterfaceType.Builder, subTypes: List<KClass<*>>): GraphQLInterfaceType {
    kClass.getValidSuperclasses(generator.config.hooks)
        .map { generateGraphQLType(generator, it.createType()) }
        .forEach {
            when (val unwrappedType = it.unwrapType()) {
                is GraphQLTypeReference -> builder.withInterface(unwrappedType)
                is GraphQLInterfaceType -> builder.withInterface(unwrappedType)
            }
        }

    kClass.getValidProperties(generator.config.hooks)
        .forEach { builder.field(generateProperty(generator, it, kClass)) }

    kClass.getValidFunctions(generator.config.hooks)
        .forEach { builder.field(generateFunction(generator, it, kClass.getSimpleName(), null, abstract = true)) }

    subTypes.forEach { generator.additionalTypes.add(AdditionalType(it.createType(), inputType = false)) }

    val interfaceType = builder.build()
    generator.codeRegistry.typeResolver(interfaceType) { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.kotlin.getSimpleName()) }
    return generator.config.hooks.onRewireGraphQLType(interfaceType, null, generator.codeRegistry).safeCast()
}
