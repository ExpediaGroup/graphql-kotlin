package com.expediagroup.webclient

import com.expediagroup.generated.TestQuery
import com.expediagroup.generated.enums.ExampleEnum
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
open class ApplicationTest(@LocalServerPort val serverPort: Int) {

    @Test
    fun `verify webclient can execute query`() {
        val client = GraphQLWebClient(url = "http://localhost:$serverPort/graphql")

        runBlocking {
            val result = client.execute(TestQuery(variables = TestQuery.Variables(name = OptionalInput.Defined("junit"))))

            assertNotNull(result)
            assertEquals("Hello World!", result.data?.helloWorld)
            assertEquals("Hello junit!", result.data?.helloJunit)

            val testObject = result.data?.objectQuery
            assertNotNull(testObject)
            assertNotNull(testObject.id)
            assertNull(testObject.description)
            assertEquals(123, testObject.count)
            assertFalse(testObject.flag)
            assertEquals(ExampleEnum.ONE.name, testObject.choice.name)
        }
    }
}
