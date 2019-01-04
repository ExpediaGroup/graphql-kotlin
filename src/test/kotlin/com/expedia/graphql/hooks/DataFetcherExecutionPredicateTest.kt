package com.expedia.graphql.hooks

import com.expedia.graphql.exceptions.GraphQLKotlinException
import com.expedia.graphql.generator.types.TypeTestHelper
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.reflect.KParameter
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class DataFetcherExecutionPredicateTest : TypeTestHelper() {

    @Test
    fun `when test() returns true, it returns the value passed in`() {
        val mockParameter: KParameter = mockk()
        val mockEnvironment: DataFetchingEnvironment = mockk()
        val predicate = spyk<DataFetcherExecutionPredicate>()

        every { predicate.test(any()) } returns true
        every { predicate.evaluate(any<Int>(), any(), any()) } returns 2

        val result = predicate.execute(1, mockParameter, mockEnvironment)

        assertEquals(expected = 1, actual = result)
        verify(exactly = 1) { predicate.test(eq(2)) }
        verify(exactly = 1) { predicate.evaluate(eq(1), eq(mockParameter), eq(mockEnvironment)) }
        verify(exactly = 0) { predicate.onFailure(any(), any(), any()) }
    }

    @Test
    fun `when test() returns false, it returns calls onFailure`() {
        val mockParameter: KParameter = mockk()
        val mockEnvironment: DataFetchingEnvironment = mockk()
        val predicate = spyk<DataFetcherExecutionPredicate>()

        every { predicate.test(any<Int>()) } returns false
        every { predicate.evaluate(any<Int>(), any(), any()) } returns 2
        every { predicate.onFailure(any<Int>(), any(), any()) } throws GraphQLKotlinException()

        assertFailsWith(GraphQLKotlinException::class) {
            predicate.execute(1, mockParameter, mockEnvironment)
        }

        verify(exactly = 1) { predicate.test(eq(2)) }
        verify(exactly = 1) { predicate.evaluate(eq(1), eq(mockParameter), eq(mockEnvironment)) }
        verify(exactly = 1) { predicate.onFailure(eq(2), eq(mockParameter), eq(mockEnvironment)) }
    }
}
