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

package com.expediagroup.graphql.dataloader.instrumentation.extensions

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.instrumentation.exceptions.MissingKotlinDataLoaderRegistryException
import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoader

/**
 * get an instance of [KotlinDataLoaderRegistry] from the [GraphQLContext]
 * @return [KotlinDataLoaderRegistry] instance
 * @throws [MissingKotlinDataLoaderRegistryException] if there is not a [KotlinDataLoaderRegistry] instance in the [GraphQLContext]
 */
fun <K, V> DataFetchingEnvironment.getDataLoaderFromContext(key: String): DataLoader<K, V> =
    this.graphQlContext
        .get<KotlinDataLoaderRegistry>(KotlinDataLoaderRegistry::class)
        ?.getDataLoader(key)
        ?: throw MissingKotlinDataLoaderRegistryException()
