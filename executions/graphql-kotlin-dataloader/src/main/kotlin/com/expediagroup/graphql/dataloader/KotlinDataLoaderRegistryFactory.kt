/*
 * Copyright 2025 Expedia, Inc
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
import org.dataloader.instrumentation.DataLoaderInstrumentation

/**
 * Generates a [DataLoaderRegistry] with the configuration provided by all [KotlinDataLoader]s.
 */
class KotlinDataLoaderRegistryFactory(
    private val dataLoaders: List<KotlinDataLoader<*, *>>
) {
    constructor() : this(emptyList())
    constructor(vararg dataLoaders: KotlinDataLoader<*, *>) : this(dataLoaders.toList())

    /**
     * Generate [DataLoaderRegistry] to be used for GraphQL request execution.
     */
    fun generate(
        graphQLContext: GraphQLContext,
        dataLoaderInstrumentation: DataLoaderInstrumentation? = null,
    ): DataLoaderRegistry {
        val builder = DataLoaderRegistry.newRegistry()
        dataLoaderInstrumentation?.let {
            builder.instrumentation(dataLoaderInstrumentation)
        }
        dataLoaders.forEach { kotlinDataLoader ->
            builder.register(
                kotlinDataLoader.dataLoaderName,
                kotlinDataLoader.getDataLoader(graphQLContext)
            )
        }
        return builder.build().also {
            graphQLContext.put(DataLoaderRegistry::class, it)
        }
    }
}
