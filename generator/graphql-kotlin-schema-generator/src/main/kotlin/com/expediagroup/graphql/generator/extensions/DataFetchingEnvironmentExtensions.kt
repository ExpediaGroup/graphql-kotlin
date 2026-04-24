/*
 * Copyright 2024 Expedia, Inc
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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.generator.execution.convertInputMap
import graphql.schema.DataFetchingEnvironment
import kotlin.reflect.KClass

/**
 * Coerces [DataFetchingEnvironment.getArguments] into a typed Kotlin object using Kotlin reflection.
 *
 * The target class must match the top-level shape of the arguments map: its constructor parameters
 * must correspond to GraphQL argument names.
 *
 * Field names are resolved using [@GraphQLName][com.expediagroup.graphql.generator.annotations.GraphQLName]
 * or the Kotlin parameter name — the same logic used to build the schema. Already-coerced values
 * (e.g. custom scalars that graphql-java has already parsed) are passed through as-is.
 *
 * This is the same coercion path that [com.expediagroup.graphql.generator.execution.FunctionDataFetcher]
 * uses internally for resolver parameters, and is the correct alternative to `ObjectMapper.convertValue`
 * for use in instrumentation or custom data fetcher code.
 */
fun <T : Any> DataFetchingEnvironment.getArgumentsAs(targetClass: KClass<T>): T =
    convertInputMap(arguments, targetClass)

/**
 * Coerces [DataFetchingEnvironment.getArguments] into a typed Kotlin object using Kotlin reflection.
 *
 * @see getArgumentsAs
 */
inline fun <reified T : Any> DataFetchingEnvironment.getArgumentsAs(): T =
    getArgumentsAs(T::class)
