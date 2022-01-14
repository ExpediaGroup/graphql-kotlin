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

package com.expediagroup.graphql.generator.internal.types.utils

import com.expediagroup.graphql.generator.internal.extensions.getTypeOfFirstArgument
import com.expediagroup.graphql.generator.internal.extensions.isSubclassOf
import graphql.execution.DataFetcherResult
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType

/**
 * These are the classes that can be returned from data fetchers (ie functions)
 * but we only want to expose the wrapped type in the schema.
 *
 * [Publisher] is used for subscriptions
 * [CompletableFuture] is used for asynchronous results
 * [DataFetcherResult] is used for returning data and errors in the same response
 *
 * We can return the following combination of types:
 *      Valid type T
 *      DataFetcherResult<T>
 *      Publisher<T>
 *      Publisher<DataFetcherResult<T>>
 *      CompletableFuture<T>
 *      CompletableFuture<DataFetcherResult<T>>
 *      List<DataFetcherResult<T>>
 *      DataFetcherResult<List<DataFetcherResult<T>>>
 */
internal fun getWrappedReturnType(returnType: KType): KType {
    return when {
        returnType.isSubclassOf(DataFetcherResult::class) && returnType.getTypeOfFirstArgument().isSubclassOf(List::class) -> getWrappedReturnType(returnType.getTypeOfFirstArgument())
        returnType.isSubclassOf(DataFetcherResult::class) -> returnType.getTypeOfFirstArgument()
        returnType.isSubclassOf(Publisher::class) -> { checkTypeForDataFetcherResult(returnType) }
        returnType.isSubclassOf(CompletableFuture::class) -> { checkTypeForDataFetcherResult(returnType) }
        returnType.isSubclassOf(List::class) && returnType.getTypeOfFirstArgument().isSubclassOf(DataFetcherResult::class) -> {
            List::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, getWrappedReturnType(returnType.getTypeOfFirstArgument()))))
        }
        else -> returnType
    }
}

/**
 * Both Publisher and CompletableFuture can be optional wrapped internally with DataFetcherResult.
 *
 *      Publisher<T>
 *      Published<DataFetcherResult<T>>
 *      CompletableFuture<T>
 *      CompletableFuture<DataFetcherResult<T>>
 *      List<DataFetcherResult<T>>
 */
private fun checkTypeForDataFetcherResult(returnType: KType): KType {
    val wrappedType = returnType.getTypeOfFirstArgument()

    return if (wrappedType.isSubclassOf(DataFetcherResult::class)) {
        wrappedType.getTypeOfFirstArgument()
    } else {
        wrappedType
    }
}
