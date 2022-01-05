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

package com.expediagroup.graphql.generator.federation.execution

import graphql.execution.DataFetcherResult
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

/**
 * [EntityResolver] that uses the `GlobalScope` coroutine for resolving the Entities
 */
open class GlobalScopeEntityResolver(resolvers: List<FederatedTypeResolver<*>>) : EntityResolver<CompletableFuture<DataFetcherResult<List<Any?>>>> {

    override val resolverMap: Map<String, FederatedTypeResolver<*>> = resolvers.associateBy { it.typeName }

    override fun get(env: DataFetchingEnvironment): CompletableFuture<DataFetcherResult<List<Any?>>> {
        return GlobalScope.future {
            resolve(env)
        }
    }
}
