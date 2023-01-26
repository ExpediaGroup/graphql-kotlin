package com.expediagroup.ktor.jackson

import com.expediagroup.generated.TestQuery
import com.expediagroup.generated.enums.ExampleEnum
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ApplicationTest {
    @Test
    fun `verify ktor client can execute queries`() {
        val engine = embeddedServer(CIO, port = 0, module = Application::graphQLModule)
        try {
            engine.start()
            runBlocking {
                val port = engine.resolvedConnectors().first().port
                val client = GraphQLKtorClient(url = URL("http://localhost:$port/graphql"))

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
        } finally {
            engine.stop(1000, 1000)
        }
    }
}
