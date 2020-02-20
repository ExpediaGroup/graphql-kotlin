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

package com.expediagroup.graphql.generator.types.utils

import graphql.execution.DataFetcherResult
import io.reactivex.rxjava3.core.Flowable
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals

internal class FunctionReturnTypesKtTest {

    internal class MyClass {
        val string = "my string"

        val publisher: Publisher<String> = Flowable.just(string)
        val flowable: Flowable<String> = Flowable.just(string)
        val dataFetcherResult: DataFetcherResult<String> = DataFetcherResult.newResult<String>().data(string).build()
        val completableFuture: CompletableFuture<String> = CompletableFuture.completedFuture(string)

        val invalidPublisher: Publisher<DataFetcherResult<String>> = Flowable.just(dataFetcherResult)
        val invalidDataFetcherResult: DataFetcherResult<CompletableFuture<String>> = DataFetcherResult.newResult<CompletableFuture<String>>().data(completableFuture).build()
        val validCompletableFutureDataFetcher: CompletableFuture<DataFetcherResult<String>> = CompletableFuture.completedFuture(dataFetcherResult)
        val invalidCompletableFuture: CompletableFuture<Publisher<String>> = CompletableFuture.completedFuture(publisher)
    }

    @Test
    fun `getWrappedReturnType of Publisher`() {
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::publisher.returnType))
        assertEquals(MyClass::dataFetcherResult.returnType, actual = getWrappedReturnType(MyClass::invalidPublisher.returnType))
    }

    @Test
    fun `getWrappedReturnType of DataFetcherResult`() {
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::dataFetcherResult.returnType))
        assertEquals(MyClass::completableFuture.returnType, actual = getWrappedReturnType(MyClass::invalidDataFetcherResult.returnType))
    }

    @Test
    fun `getWrappedReturnType of CompletableFuture`() {
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::completableFuture.returnType))
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::validCompletableFutureDataFetcher.returnType))
        assertEquals(MyClass::publisher.returnType, actual = getWrappedReturnType(MyClass::invalidCompletableFuture.returnType))
    }

    @Test
    fun `getWrappedReturnType of String`() {
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::string.returnType))
    }

    @Test
    fun `getWrappedReturnType of type that implements Publisher`() {
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::flowable.returnType))
    }
}
