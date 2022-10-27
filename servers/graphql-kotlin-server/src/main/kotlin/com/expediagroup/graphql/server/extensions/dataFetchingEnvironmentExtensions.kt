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

package com.expediagroup.graphql.server.extensions

import com.expediagroup.graphql.generator.exceptions.KeyNotFoundInGraphQLContextException
import com.expediagroup.graphql.generator.extensions.get
import com.expediagroup.graphql.generator.extensions.getOrDefault
import com.expediagroup.graphql.generator.extensions.getOrElse
import com.expediagroup.graphql.generator.extensions.getOrThrow
import com.expediagroup.graphql.server.exception.MissingDataLoaderException
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

/**
 * Helper method to get a value from a registered DataLoader.
 * The provided key should be the cache key object used to save the value for that particular data loader.
 */
@Deprecated(
    message = "Custom DataLoader extension functions are deprecated and will be removed in next major release",
    replaceWith = ReplaceWith("getDataLoader<K, V>(dataLoaderName)")
)
fun <K, V> DataFetchingEnvironment.getValueFromDataLoader(dataLoaderName: String, key: K): CompletableFuture<V> {
    val loader = getDataLoader<K, V>(dataLoaderName) ?: throw MissingDataLoaderException(dataLoaderName)
    return loader.load(key, this.graphQlContext)
}

/**
* Helper method to get values from a registered DataLoader.
*/
@Deprecated(
    message = "Custom DataLoader extension functions are deprecated and will be removed in next major release",
    replaceWith = ReplaceWith("getDataLoader<K, V>(dataLoaderName)")
)
fun <K, V> DataFetchingEnvironment.getValuesFromDataLoader(dataLoaderName: String, keys: List<K>): CompletableFuture<List<V>> {
    val loader = getDataLoader<K, V>(dataLoaderName) ?: throw MissingDataLoaderException(dataLoaderName)
    return loader.loadMany(keys, listOf(this.graphQlContext))
}

/**
 * Returns a value from the graphQLContext by KClass key
 * @return a value or null
 */
inline fun <reified T> DataFetchingEnvironment.getFromContext(): T? =
    graphQlContext.get<T>()

/**
 * Returns a value from the graphQLContext by KClass key
 * @param defaultValue the default value to use if there is no KClass key entry
 * @return a value or default value
 */
inline fun <reified T> DataFetchingEnvironment.getFromContextOrDefault(defaultValue: T): T =
    graphQlContext.getOrDefault(defaultValue)

/**
 * Returns a value from the graphQLContext by KClass key
 * @param defaultValue function to invoke if there is no KClass key entry
 * @return a value or result of [defaultValue] function
 */
inline fun <reified T> DataFetchingEnvironment.getFromContextOrElse(defaultValue: () -> T): T =
    graphQlContext.getOrElse(defaultValue)

/**
 * Returns a value from the graphQLContext by KClass key or [KeyNotFoundInGraphQLContextException] if key was not found
 * @return a value or [KeyNotFoundInGraphQLContextException]
 */
inline fun <reified T> DataFetchingEnvironment.getFromContextOrThrow(): T =
    graphQlContext.getOrThrow()
