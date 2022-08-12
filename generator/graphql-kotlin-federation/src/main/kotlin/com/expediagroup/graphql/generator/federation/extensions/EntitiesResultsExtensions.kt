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

package com.expediagroup.graphql.generator.federation.extensions

import graphql.GraphQLError
import graphql.execution.DataFetcherResult

internal fun Sequence<Any?>.toDataFetcherResult(): DataFetcherResult<List<Any?>> {
    val data = mutableListOf<Any?>()
    val errors = mutableListOf<GraphQLError>()

    forEach { result ->
        if (result is GraphQLError) {
            data += null
            errors += result
        } else {
            data += result
        }
    }

    return DataFetcherResult.newResult<List<Any?>>()
        .data(data)
        .errors(errors)
        .build()
}
