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

import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.DataLoader

/**
 * get the [DataFetchingEnvironment] from the [BatchLoaderEnvironment], in order to access to it,
 * you need to pass your [DataFetchingEnvironment] to the [DataLoader.load] as second argument
 * Example:
 * ```
 * dataloader.load(yourKey, dataFetchingEnvironment)
 * ```
 */
fun BatchLoaderEnvironment.getDataFetchingEnvironment(): DataFetchingEnvironment? =
    keyContextsList.firstOrNull() as? DataFetchingEnvironment

/**
 * get the [GraphQLContext] from the [BatchLoaderEnvironment], in order to access to it,
 * you need to pass your [DataFetchingEnvironment] to the [DataLoader.load] as second argument
 * Example:
 * ```
 * dataloader.load(yourKey, dataFetchingEnvironment)
 * ```
 */
fun BatchLoaderEnvironment.getGraphQLContext(): GraphQLContext? =
    getDataFetchingEnvironment()?.graphQlContext
