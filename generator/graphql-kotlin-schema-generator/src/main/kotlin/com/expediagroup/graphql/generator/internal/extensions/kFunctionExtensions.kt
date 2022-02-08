/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters

private val logger: Logger = LoggerFactory.getLogger("schemaGenerator")

internal fun KFunction<*>.getValidArguments(parentName: String): List<KParameter> = this.valueParameters.mapNotNull {
    when {
        it.isGraphQLIgnored() || it.isDataFetchingEnvironment() -> null
        it.isGraphQLContext() -> {
            logger.warn("$parentName.${getFunctionName()} relies on GraphQLContext injection which is deprecated. Please update it to retrieve context from DataFetchingEnvironment instead.")
            null
        }
        else -> it
    }
}
