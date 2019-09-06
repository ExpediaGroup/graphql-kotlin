package com.expediagroup.graphql.federation.execution

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ResolversKtTest {

    @Test
    fun `resolveBatch should call the resolver from the registry`() {
        val indexedValue = mapOf<String, Any>()
        val indexedRequests: List<IndexedValue<Map<String, Any>>> = listOf(IndexedValue(7, indexedValue))
        val mockResolver: FederatedTypeResolver<*> = mockk()
        coEvery { mockResolver.resolve(any()) } returns listOf("foo")
        val registry = FederatedTypeRegistry(mapOf("MyType" to mockResolver))

        runBlocking {
            val result = resolveType("MyType", indexedRequests, registry)
            assertTrue(result.isNotEmpty())
            assertEquals(expected = 7 to "foo", actual = result.first())
            coVerify(exactly = 1) { mockResolver.resolve(any()) }
        }
    }
}
