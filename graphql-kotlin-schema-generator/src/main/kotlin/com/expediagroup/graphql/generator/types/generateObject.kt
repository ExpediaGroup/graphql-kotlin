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

import com.expediagroup.graphql.extensions.unwrapType
import com.expediagroup.graphql.generator.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass

internal fun generateObject(generator: TypeGenerator, kClass: KClass<*>): GraphQLObjectType {
    val builder: GraphQLObjectType.Builder = GraphQLObjectType.newObject()

    builder.name(kClass.getSimpleName())
    builder.description(kClass.getGraphQLDescription())

    generateDirectives(generator, kClass).forEach {
        builder.withDirective(it)
    }

    generateSuperclasses(generator, kClass).forEach {
        when (val unwrappedType = it.unwrapType()) {
            is GraphQLTypeReference -> builder.withInterface(unwrappedType)
            is GraphQLInterfaceType -> builder.withInterface(unwrappedType)
        }
    }

    generateProperties(generator, kClass).forEach {
        builder.field(it)
    }

    generateFunctions(generator, kClass, abstract = false).forEach {
        builder.field(it)
    }

    return generator.config.hooks.onRewireGraphQLType(builder.build()).safeCast()
}
