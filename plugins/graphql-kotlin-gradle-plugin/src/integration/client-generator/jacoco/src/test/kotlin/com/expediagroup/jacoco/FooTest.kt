package com.expediagroup.jacoco

import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FooTest {

    @Test
    fun `simple test for excluding generated code from coverage`() = runBlocking {
        val mockkClient = mockk<GraphQLWebClient> {
            coEvery { execute(any<GraphQLClientRequest<String>>(), any()) } returns JacksonGraphQLResponse(data = "Bar")
        }
        val foo = Foo(mockkClient)
        Assertions.assertEquals("Bar", foo.query())
    }
}
