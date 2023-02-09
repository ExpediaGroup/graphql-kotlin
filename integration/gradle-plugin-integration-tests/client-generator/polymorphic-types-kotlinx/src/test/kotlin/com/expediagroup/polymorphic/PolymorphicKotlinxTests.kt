package com.expediagroup.polymorphic

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.serialization.GraphQLClientKotlinxSerializer
import com.expediagroup.polymorphic.generated.CompletePolymorphicQuery
import com.expediagroup.polymorphic.generated.PartialPolymorphicQuery
import com.expediagroup.polymorphic.generated.completepolymorphicquery.Foo
import com.expediagroup.polymorphic.generated.completepolymorphicquery.FooImplementation
import com.expediagroup.polymorphic.generated.partialpolymorphicquery.BasicInterface
import com.expediagroup.polymorphic.generated.partialpolymorphicquery.BasicUnion
import com.expediagroup.polymorphic.generated.partialpolymorphicquery.DefaultBasicInterfaceImplementation
import com.expediagroup.polymorphic.generated.partialpolymorphicquery.DefaultBasicUnionImplementation
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import java.net.URL
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.junit.jupiter.api.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PolymorphicKotlinxTests {

    @Test
    fun `verify polymorphic queries are correctly serialized and deserialized`() {
        val engine = embeddedServer(CIO, port = 0, module = Application::graphQLModule)
        try {
            engine.start()
            runBlocking {
                val port = engine.resolvedConnectors().first().port
                val client = GraphQLKtorClient(url = URL("http://localhost:$port/graphql"))

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
        } finally {
            engine.stop(1000, 1000)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `verify polymorphic queries fallbacks are correctly serialized and deserialized`() {
        val engine = embeddedServer(CIO, port = 0, module = Application::graphQLModule)
        try {
            engine.start()
            runBlocking {
                // need to register fallback logic
                val serializerWithFallback = GraphQLClientKotlinxSerializer(jsonBuilder = {
                    serializersModule = SerializersModule {
                        polymorphic(BasicInterface::class) {
                            defaultDeserializer { DefaultBasicInterfaceImplementation.serializer() }
                        }
                        polymorphic(BasicUnion::class) {
                            defaultDeserializer { DefaultBasicUnionImplementation.serializer() }
                        }
                    }
                })
                val port = engine.resolvedConnectors().first().port
                val client = GraphQLKtorClient(url = URL("http://localhost:$port/graphql"), serializer = serializerWithFallback)

                val fallbackQuery = PartialPolymorphicQuery(variables = PartialPolymorphicQuery.Variables(input = "bar"))
                val fallbackResponse = client.execute(fallbackQuery)
                val interfaceResult = fallbackResponse.data?.interfaceQuery
                assertTrue(interfaceResult is DefaultBasicInterfaceImplementation)
                val unionResult = fallbackResponse.data?.unionQuery
                assertTrue(unionResult is DefaultBasicUnionImplementation)
            }
        } finally {
            engine.stop(1000, 1000)
        }
    }
}
