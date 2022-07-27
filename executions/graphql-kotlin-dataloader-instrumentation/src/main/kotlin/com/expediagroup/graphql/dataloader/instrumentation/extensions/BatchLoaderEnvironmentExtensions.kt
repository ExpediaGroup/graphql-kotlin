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
