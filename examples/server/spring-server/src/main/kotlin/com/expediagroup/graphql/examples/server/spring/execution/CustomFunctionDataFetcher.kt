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

package com.expediagroup.graphql.examples.server.spring.execution

import com.expediagroup.graphql.server.spring.execution.SpringDataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.context.ApplicationContext
import reactor.core.publisher.Mono
import kotlin.reflect.KFunction

/**
 * Custom function data fetcher that adds support for Reactor Mono.
 */
class CustomFunctionDataFetcher(
    target: Any?,
    fn: KFunction<*>,
    appContext: ApplicationContext
) : SpringDataFetcher(target, fn, appContext) {

    override fun get(environment: DataFetchingEnvironment): Any? = when (val result = super.get(environment)) {
        is Mono<*> -> result.toFuture()
        else -> result
    }
}
