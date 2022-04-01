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
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.internal.extensions.getValidFunctions
import com.expediagroup.graphql.generator.internal.extensions.getValidProperties
import com.expediagroup.graphql.generator.internal.extensions.getValidSuperclasses
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.internal.state.AdditionalType
import com.expediagroup.graphql.generator.internal.types.utils.validateGraphQLName
import graphql.TypeResolutionEnvironment
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

internal fun generateInterface(generator: SchemaGenerator, kClass: KClass<*>): GraphQLInterfaceType {
    val name = kClass.getSimpleName()
    validateGraphQLName(name, kClass)

    val builder = GraphQLInterfaceType.newInterface()
    builder.name(name)
    builder.description(kClass.getGraphQLDescription())

    generateDirectives(generator, kClass, DirectiveLocation.INTERFACE).forEach {
        builder.withAppliedDirective(it)
    }

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

    generator.classScanner.getSubTypesOf(kClass)
        .filter { generator.config.hooks.isValidAdditionalType(it, inputType = false) }
        .forEach { generator.additionalTypes.add(AdditionalType(it.createType(), inputType = false)) }

    val interfaceType: GraphQLInterfaceType = builder.build()
    generator.codeRegistry.typeResolver(interfaceType) { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObject<Any>().javaClass.kotlin.getSimpleName()) }
    return generator.config.hooks.onRewireGraphQLType(interfaceType, null, generator.codeRegistry).safeCast()
}
