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
import com.expediagroup.graphql.generator.internal.extensions.getKClass
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.scalars.ID
import graphql.Scalars
import graphql.schema.GraphQLScalarType
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal fun generateScalar(generator: SchemaGenerator, type: KType): GraphQLScalarType? {
    val kClass: KClass<*> = type.getKClass()
    val scalar: GraphQLScalarType? = defaultScalarsMap[kClass]

    return scalar?.let {
        generator.config.hooks.onRewireGraphQLType(it, null, generator.codeRegistry).safeCast()
    }
}

private val defaultScalarsMap = mapOf(
    Int::class to Scalars.GraphQLInt,
    Float::class to Scalars.GraphQLFloat,
    Double::class to Scalars.GraphQLFloat,
    String::class to Scalars.GraphQLString,
    Boolean::class to Scalars.GraphQLBoolean,
    ID::class to Scalars.GraphQLID
)
