package com.expedia.graphql

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.hooks.DataFetcherExecutionPredicate
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class KotlinDataFetcherTest {

    internal class MyClass {
        fun print(string: String) = string

        fun context(@GraphQLContext string: String) = string
    }

    @Test
    fun `null target and null source returns null`() {
        val dataFetcher = KotlinDataFetcher(null, MyClass::print, null)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.getSource<Any>() } returns null
        assertNull(dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `null target and valid source returns the value`() {
        val dataFetcher = KotlinDataFetcher(null, MyClass::print, null)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.getSource<Any>() } returns MyClass()
        every { mockEnvironmet.arguments } returns mapOf("string" to "hello")
        assertEquals(expected = "hello", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `valid target and null source returns the value`() {
        val dataFetcher = KotlinDataFetcher(MyClass(), MyClass::print, null)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.arguments } returns mapOf("string" to "hello")
        assertEquals(expected = "hello", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `valid target with context`() {
        val dataFetcher = KotlinDataFetcher(MyClass(), MyClass::context, null)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.getContext<String>() } returns "foo"
        assertEquals(expected = "foo", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `valid target and value from predicate`() {
        val mockPredicate: DataFetcherExecutionPredicate = mockk()
        every { mockPredicate.execute<String>(any(), any(), any()) } returns "baz"
        val dataFetcher = KotlinDataFetcher(MyClass(), MyClass::print, mockPredicate)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.arguments } returns mapOf("string" to "hello")
        assertEquals(expected = "baz", actual = dataFetcher.get(mockEnvironmet))
    }

    @Test
    fun `valid target and null from predicate`() {
        val mockPredicate: DataFetcherExecutionPredicate = mockk()
        every { mockPredicate.execute<String?>(any(), any(), any()) } returns null
        val dataFetcher = KotlinDataFetcher(MyClass(), MyClass::print, mockPredicate)
        val mockEnvironmet: DataFetchingEnvironment = mockk()
        every { mockEnvironmet.arguments } returns mapOf("string" to "hello")
        assertEquals(expected = "hello", actual = dataFetcher.get(mockEnvironmet))
    }
}
