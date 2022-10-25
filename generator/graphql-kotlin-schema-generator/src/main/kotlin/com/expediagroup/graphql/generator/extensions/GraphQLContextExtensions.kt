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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.generator.exceptions.KeyNotFoundInGraphQLContextException
import graphql.GraphQLContext

/**
 * Returns a value in the context by KClass key
 * @return a value or null
 */
inline fun <reified T> GraphQLContext.get(): T? =
    get(T::class)

/**
 * Returns a value in the context by KClass key
 * @param defaultValue the default value to use if there is no KClass key entry
 * @return a value or default value
 */
inline fun <reified T> GraphQLContext.getOrDefault(defaultValue: T): T =
    getOrDefault(T::class, defaultValue)

/**
 * Returns a value in the context by KClass key
 * @param defaultValue function to invoke if there is no KClass key entry
 * @return a value or result of [defaultValue] function
 */
inline fun <reified T> GraphQLContext.getOrElse(defaultValue: () -> T): T =
    get(T::class) ?: defaultValue.invoke()

/**
 * Returns a value in the context by KClass key or [KeyNotFoundInGraphQLContextException] if key was not found
 * @return a value or [KeyNotFoundInGraphQLContextException]
 */
inline fun <reified T> GraphQLContext.getOrThrow(): T =
    get(T::class) ?: throw KeyNotFoundInGraphQLContextException(T::class)

/**
 * Puts all the values of [graphQLContext] into [this] GraphQLContext
 * @param graphQLContext which values will be added into [this] GraphQLContext
 * @return current GraphQLContext with values of [graphQLContext] added
 */
operator fun GraphQLContext.plus(graphQLContext: GraphQLContext): GraphQLContext =
    putAll(graphQLContext)

/**
 * Puts all the values of [map] into [this] GraphQLContext
 * @param map which values will be added into [this] GraphQLContext
 * @return current GraphQLContext with values of [map] added
 */
operator fun GraphQLContext.plus(map: Map<*, Any?>): GraphQLContext =
    putAll(map)

/**
 * Create a [GraphQLContext] from [this] map
 * @return a new [GraphQLContext]
 */
fun Map<*, Any?>.toGraphQLContext(): GraphQLContext =
    GraphQLContext.of(this)
