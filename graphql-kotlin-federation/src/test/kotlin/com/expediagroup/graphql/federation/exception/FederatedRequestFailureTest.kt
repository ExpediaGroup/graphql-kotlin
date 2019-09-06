package com.expediagroup.graphql.federation.exception

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
