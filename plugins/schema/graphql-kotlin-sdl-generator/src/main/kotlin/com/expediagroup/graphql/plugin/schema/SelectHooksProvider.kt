/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.plugin.schema

import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider
import org.slf4j.Logger

/**
 * Pick the `SchemaGeneratorHooksProvider` that should be used when more than one provider is registered through
 * `ServiceLoader`.
 *
 * Selection rules:
 * * When the list contains a single provider it is returned unchanged.
 * * When the list contains multiple providers the one with the strictly highest `priority()` wins.
 * * When multiple providers tie for the highest priority a `RuntimeException` is thrown so the conflict is surfaced
 *   to the user, preserving the fail-fast behaviour that existed before priorities were supported.
 *
 * The caller is expected to handle the empty-list case before invoking this function.
 */
internal fun selectHooksProvider(
    providers: List<SchemaGeneratorHooksProvider>,
    logger: Logger
): SchemaGeneratorHooksProvider {
    require(providers.isNotEmpty()) { "selectHooksProvider should not be called with an empty list of providers" }

    if (providers.size == 1) {
        val provider = providers.single()
        logger.debug("SchemaGeneratorHooksProvider found, ${provider.javaClass.simpleName} will be used to generate the hooks")
        return provider
    }

    val maxPriority = providers.maxOf { it.priority() }
    val topProviders = providers.filter { it.priority() == maxPriority }
    if (topProviders.size > 1) {
        val tiedNames = topProviders.joinToString(", ") { it.javaClass.name }
        throw RuntimeException(
            "Cannot generate SDL as multiple SchemaGeneratorHooksProviders were found on the classpath with " +
                "the same priority ($maxPriority): [$tiedNames]. Override SchemaGeneratorHooksProvider.priority() " +
                "on the provider that should win to disambiguate."
        )
    }

    val winner = topProviders.single()
    val losers = providers.filter { it !== winner }
    logger.warn(
        "Multiple SchemaGeneratorHooksProviders were found on the classpath; selecting ${winner.javaClass.simpleName} " +
            "with priority $maxPriority. Ignored providers: " +
            losers.joinToString(", ") { "${it.javaClass.simpleName}(priority=${it.priority()})" }
    )
    return winner
}
