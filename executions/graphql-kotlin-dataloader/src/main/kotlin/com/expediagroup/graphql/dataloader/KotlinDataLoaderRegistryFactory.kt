/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.dataloader

import graphql.GraphQLContext
import org.dataloader.DataLoaderRegistry
import org.dataloader.instrumentation.ChainedDataLoaderInstrumentation
import org.dataloader.instrumentation.DataLoaderInstrumentation

/**
 * Generates a [KotlinDataLoaderRegistry] with the configuration provided by all [KotlinDataLoader]s.
 */
class KotlinDataLoaderRegistryFactory(
    private val dataLoaders: List<KotlinDataLoader<*, *>>,
    private val dataLoaderInstrumentations: List<DataLoaderInstrumentation>
) {
    constructor(): this(emptyList(), emptyList())
    constructor(dataLoaders: List<KotlinDataLoader<*, *>>): this(dataLoaders, emptyList())
    /**
     * Generate [KotlinDataLoaderRegistry] to be used for GraphQL request execution.
     */
    fun generate(graphQLContext: GraphQLContext): KotlinDataLoaderRegistry {
        val builder = DataLoaderRegistry.newRegistry()
        builder.instrumentation(
            ChainedDataLoaderInstrumentation(
                dataLoaderInstrumentations.toMutableList().also {
                    it.add(DataLoaderDependantsStateInstrumentation(graphQLContext))
                }
            )
        )
        dataLoaders.forEach { kotlinDataLoader ->
            builder.register(
                kotlinDataLoader.dataLoaderName,
                kotlinDataLoader.getDataLoader(graphQLContext)
            )
        }
        return KotlinDataLoaderRegistry(builder.build()).also {
            graphQLContext.put(KotlinDataLoaderRegistry::class, it)
        }
    }
}
