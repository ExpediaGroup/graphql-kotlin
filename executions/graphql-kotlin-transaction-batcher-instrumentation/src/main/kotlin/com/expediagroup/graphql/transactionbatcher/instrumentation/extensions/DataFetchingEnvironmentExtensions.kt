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

package com.expediagroup.graphql.transactionbatcher.instrumentation.extensions

import com.expediagroup.graphql.transactionbatcher.instrumentation.TransactionLoader
import com.expediagroup.graphql.transactionbatcher.instrumentation.exceptions.MissingTransactionLoaderException
import graphql.schema.DataFetchingEnvironment

/**
 * get an implementation instance of [TransactionLoader] from the GraphQLContext
 * @return [TransactionLoader] loader implementation instance
 * @throws [MissingTransactionLoaderException] if there is not a [TransactionLoader] implementation instance in the GraphQLContext
 */
inline fun <reified T : Any> DataFetchingEnvironment.getTransactionLoader(): T =
    this.graphQlContext
        .get<TransactionLoader<out T>>(TransactionLoader::class)
        ?.loader
        ?: throw MissingTransactionLoaderException()
