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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.generator.extensions.getValidFunctions
import com.expediagroup.graphql.generator.extensions.getValidProperties
import com.expediagroup.graphql.generator.extensions.getValidSuperclasses
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLTypeUtil
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

internal class ObjectBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun objectType(kClass: KClass<*>): GraphQLType {
        return state.cache.buildIfNotUnderConstruction(kClass) {
            val builder = GraphQLObjectType.newObject()

            val name = kClass.getSimpleName()
            builder.name(name)
            builder.description(kClass.getGraphQLDescription())

            generator.directives(kClass).forEach {
                builder.withDirective(it)
            }

            kClass.getValidSuperclasses(config.hooks)
                .map { graphQLTypeOf(it.createType()) }
                .forEach {
                    when (val unwrappedType = GraphQLTypeUtil.unwrapNonNull(it)) {
                        is GraphQLTypeReference -> builder.withInterface(unwrappedType)
                        is GraphQLInterfaceType -> builder.withInterface(unwrappedType)
                    }
                }

            kClass.getValidProperties(config.hooks)
                .forEach { builder.field(generator.property(it, kClass)) }

            kClass.getValidFunctions(config.hooks)
                .forEach { builder.field(generator.function(it, name)) }

            extensionFunctionMapper.getValidExtensionFunctions(kClass)
                .forEach { builder.field(generator.function(it, name)) }

            config.hooks.onRewireGraphQLType(builder.build()).safeCast()
        }
    }
}
