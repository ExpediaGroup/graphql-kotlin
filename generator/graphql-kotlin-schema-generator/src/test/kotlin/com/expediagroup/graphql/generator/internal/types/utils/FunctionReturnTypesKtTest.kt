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

package com.expediagroup.graphql.generator.internal.types.utils

import graphql.execution.DataFetcherResult
import io.reactivex.rxjava3.core.Flowable
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals

class FunctionReturnTypesKtTest {

    class MyClass {
        // Valid Types
        val string = "my string"
        val listOfString: List<String> = listOf(string)
        val listOfListString: List<List<String>> = listOf(listOf(string))
        val dataFetcherResult: DataFetcherResult<String> = DataFetcherResult.newResult<String>().data(string).build()
        val listDataFetcherResult: List<DataFetcherResult<String>> = listOf(DataFetcherResult.newResult<String>().data(string).build())
        val dataFetcherResultListDataFetcherResult: DataFetcherResult<List<DataFetcherResult<String>>> =
            DataFetcherResult.newResult<List<DataFetcherResult<String>>>().data(listOf(DataFetcherResult.newResult<String>().data(string).build())).build()
        val dataFetcherResultListString: DataFetcherResult<List<String>> = DataFetcherResult.newResult<List<String>>().data(listOf(string)).build()
        val listDataFetcherResultListString: List<DataFetcherResult<List<String>>> = listOf(DataFetcherResult.newResult<List<String>>().data(listOf(string)).build())
        val publisher: Publisher<String> = Flowable.just(string)
        val flowable: Flowable<String> = Flowable.just(string)
        val publisherDataFetcherResult: Publisher<DataFetcherResult<String>> = Flowable.just(dataFetcherResult)
        val completableFuture: CompletableFuture<String> = CompletableFuture.completedFuture(string)
        val completableFutureDataFetcher: CompletableFuture<DataFetcherResult<String>> = CompletableFuture.completedFuture(dataFetcherResult)

        // Invalid types
        val invalidDataFetcherResultCompletableFuture: DataFetcherResult<CompletableFuture<String>> = DataFetcherResult.newResult<CompletableFuture<String>>().data(completableFuture).build()
        val invalidDataFetcherResultPublisher: DataFetcherResult<Publisher<String>> = DataFetcherResult.newResult<Publisher<String>>().data(publisher).build()
        val invalidCompletableFuture: CompletableFuture<Publisher<String>> = CompletableFuture.completedFuture(publisher)
    }

    @Test
    fun `getWrappedReturnType of lists`() {
        assertEquals(MyClass::listOfString.returnType, actual = getWrappedReturnType(MyClass::listOfString.returnType))
        assertEquals(MyClass::listOfListString.returnType, actual = getWrappedReturnType(MyClass::listOfListString.returnType))
    }

    @Test
    fun `getWrappedReturnType of Publisher`() {
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::publisher.returnType))
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::publisherDataFetcherResult.returnType))
    }

    @Test
    fun `getWrappedReturnType of DataFetcherResult`() {
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::dataFetcherResult.returnType))
        assertEquals(MyClass::completableFuture.returnType, actual = getWrappedReturnType(MyClass::invalidDataFetcherResultCompletableFuture.returnType))
        assertEquals(MyClass::publisher.returnType, actual = getWrappedReturnType(MyClass::invalidDataFetcherResultPublisher.returnType))
    }

    @Test
    fun `getWrappedReturnType of List of DataFetcherResult`() {
        assertEquals(MyClass::listOfString.returnType, actual = getWrappedReturnType(MyClass::listDataFetcherResult.returnType))
        assertEquals(MyClass::listOfString.returnType, actual = getWrappedReturnType(MyClass::dataFetcherResultListString.returnType))
        assertEquals(MyClass::listOfListString.returnType, actual = getWrappedReturnType(MyClass::listDataFetcherResultListString.returnType))
    }

    @Test
    fun `getWrappedReturnType of DataFetcherResult of List of DataFetcherResult`() {
        assertEquals(MyClass::listOfString.returnType, actual = getWrappedReturnType(MyClass::dataFetcherResultListDataFetcherResult.returnType))
    }

    @Test
    fun `getWrappedReturnType of CompletableFuture`() {
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::completableFuture.returnType))
        assertEquals(MyClass::string.returnType, actual = getWrappedReturnType(MyClass::completableFutureDataFetcher.returnType))
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
