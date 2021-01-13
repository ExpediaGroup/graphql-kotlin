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

package com.expediagroup.graphql.generator.federation.exception

import graphql.ErrorType
import graphql.language.SourceLocation
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class FederatedRequestFailureTest {

    private val simpleFailure = FederatedRequestFailure("myErrorMessage")

    @Test
    fun getMessage() {
        assertEquals(expected = "myErrorMessage", actual = simpleFailure.message)
    }

    @Test
    fun getErrorType() {
        assertEquals(expected = ErrorType.DataFetchingException, actual = simpleFailure.errorType)
    }

    @Test
    fun getLocations() {
        assertEquals(expected = listOf(SourceLocation(-1, -1)), actual = simpleFailure.locations)
    }

    @Test
    fun getExtensions() {
        assertNull(simpleFailure.extensions)

        val exception = Exception("custom error")
        val failure = FederatedRequestFailure("newErrorMessage", exception)
        assertEquals(expected = mapOf("error" to exception), actual = failure.extensions)
    }
}
