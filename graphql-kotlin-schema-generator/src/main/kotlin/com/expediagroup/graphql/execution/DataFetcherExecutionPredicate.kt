/*
 * Copyright 2019 Expedia Group
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

package com.expediagroup.graphql.execution

import graphql.schema.DataFetchingEnvironment
import kotlin.reflect.KParameter

/**
 * Perform runtime evaluations of each parameter passed to any FunctionDataFetcher.
 *
 * The DataFetcherExecutionPredicate is declared globally for all the datafetchers instances and all the parameters.
 * However a more precise logic (at the field level) is possible depending on the implement of `evaluate`
 *
 * Because the DataFetcherExecutionPredicate is global, it's not possible to have methods where the type is inferred.
 *
 * It's recommended to check the type of the different arguments.
 */
interface DataFetcherExecutionPredicate {

    /**
     * Perform the predicate logic by evaluating the argument and its value.
     * Then depending on the result either:
     *   - Return the value itself to continue the datafetcher invocation
     *   - Throw an exception
     *
     * @param value the value to evaluate the predicate against
     * @param parameter the function argument reference containing the KClass and the argument annotations
     * @param environment the DataFetchingEnvironment in which the data fetcher is executed (gives access to field info, execution context etc)
     */
    fun <T> evaluate(value: T, parameter: KParameter, environment: DataFetchingEnvironment): T
}
