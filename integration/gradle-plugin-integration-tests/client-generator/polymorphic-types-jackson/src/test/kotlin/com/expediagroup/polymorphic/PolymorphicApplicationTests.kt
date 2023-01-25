package com.expediagroup.polymorphic

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.polymorphic.generated.CompletePolymorphicQuery
import com.expediagroup.polymorphic.generated.PartialPolymorphicQuery
import com.expediagroup.polymorphic.generated.completepolymorphicquery.Foo
import com.expediagroup.polymorphic.generated.completepolymorphicquery.FooImplementation
import com.expediagroup.polymorphic.generated.partialpolymorphicquery.DefaultBasicUnionImplementation
import com.expediagroup.polymorphic.generated.partialpolymorphicquery.DefaultBasicInterfaceImplementation
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomScalarApplicationTests(@LocalServerPort private val port: Int) {

    @Test
    fun `verify polymorphic queries are correctly serialized and deserialized`() = runBlocking {
        val client = GraphQLWebClient(url = "http://localhost:$port/graphql")

        val query = CompletePolymorphicQuery(variables = CompletePolymorphicQuery.Variables(input = "foo"))
        val response = client.execute(query)
        val interfaceResult = response.data?.interfaceQuery
        assertTrue(interfaceResult is FooImplementation)
        val unionResult = response.data?.unionQuery
        assertTrue(unionResult is Foo)

        val nullQuery = CompletePolymorphicQuery(variables = CompletePolymorphicQuery.Variables())
        val nullResponse = client.execute(nullQuery)
        assertNull(nullResponse.data?.interfaceQuery)
        assertNull(nullResponse.data?.unionQuery)
    }

    @Test
    fun `verify polymorphic queries fallbacks are correctly serialized and deserialized`() = runBlocking {
        val client = GraphQLWebClient(url = "http://localhost:$port/graphql")

        val fallbackQuery = PartialPolymorphicQuery(variables = PartialPolymorphicQuery.Variables(input = "bar"))
        val fallbackResponse = client.execute(fallbackQuery)
        val interfaceResult = fallbackResponse.data?.interfaceQuery
        assertTrue(interfaceResult is DefaultBasicInterfaceImplementation)
        val unionResult = fallbackResponse.data?.unionQuery
        assertTrue(unionResult is DefaultBasicUnionImplementation)
    }

}
