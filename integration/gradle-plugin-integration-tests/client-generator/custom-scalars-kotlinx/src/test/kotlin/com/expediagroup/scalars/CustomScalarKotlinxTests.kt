package com.expediagroup.scalars

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.scalars.generated.LocaleQuery
import com.expediagroup.scalars.generated.OptionalScalarQuery
import com.expediagroup.scalars.generated.RequiredScalarQuery
import com.expediagroup.scalars.generated.inputs.OptionalWrapperInput
import com.expediagroup.scalars.generated.inputs.RequiredWrapperInput
import com.expediagroup.scalars.generated.inputs.SimpleInput
import com.expediagroup.scalars.queries.UNDEFINED_BOOLEAN
import com.expediagroup.scalars.queries.UNDEFINED_DOUBLE
import com.expediagroup.scalars.queries.UNDEFINED_INT
import com.expediagroup.scalars.queries.UNDEFINED_LOCALE
import com.expediagroup.scalars.queries.UNDEFINED_OBJECT
import com.expediagroup.scalars.queries.UNDEFINED_STRING
import com.expediagroup.scalars.queries.UNDEFINED_UUID
import com.ibm.icu.util.ULocale
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import java.net.URL
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CustomScalarKotlinxTests {

    @Test
    fun `verify custom scalars are correctly serialized and deserialized`() {
        val engine = embeddedServer(CIO, port = 0, module = Application::graphQLModule)
        try {
            engine.start()
            runBlocking {
                val port = engine.resolvedConnectors().first().port
                val client = GraphQLKtorClient(url = URL("http://localhost:$port/graphql"))

                val undefinedLocaleQuery = LocaleQuery(variables = LocaleQuery.Variables())
                val undefinedLocaleResult = client.execute(undefinedLocaleQuery)
                assertEquals(ULocale.US, undefinedLocaleResult.data?.localeQuery)

                val nullLocaleQuery = LocaleQuery(variables = LocaleQuery.Variables(optional = OptionalInput.Defined(null)))
                val nullLocaleResult = client.execute(nullLocaleQuery)
                assertNull(nullLocaleResult.data?.localeQuery)

                val localeQuery = LocaleQuery(variables = LocaleQuery.Variables(optional = OptionalInput.Defined(ULocale.CANADA)))
                val localeResult = client.execute(localeQuery)
                assertEquals(ULocale.CANADA, localeResult.data?.localeQuery)
            }
        } finally {
            engine.stop(1000, 1000)
        }
    }

    @Test
    fun `verify undefined optionals are correctly serialized and deserialized`() {
        val engine = embeddedServer(CIO, port = 0, module = Application::graphQLModule)
        try {
            engine.start()
            runBlocking {
                val port = engine.resolvedConnectors().first().port
                val client = GraphQLKtorClient(url = URL("http://localhost:$port/graphql"))

                val undefinedWrapperQuery = OptionalScalarQuery(variables = OptionalScalarQuery.Variables())
                val undefinedWrapperResult = client.execute(undefinedWrapperQuery).data?.optionalScalarQuery
                assertNotNull(undefinedWrapperResult)
                assertEquals(UNDEFINED_BOOLEAN, undefinedWrapperResult.optionalBoolean)
                assertEquals(UNDEFINED_DOUBLE, undefinedWrapperResult.optionalDouble)
                assertEquals(UNDEFINED_STRING, undefinedWrapperResult.optionalId)
                assertEquals(UNDEFINED_INT, undefinedWrapperResult.optionalInt)
                assertEquals(0, undefinedWrapperResult.optionalIntList?.size)
                assertEquals(UNDEFINED_OBJECT.foo, undefinedWrapperResult.optionalObject?.foo)
                assertEquals(UNDEFINED_STRING, undefinedWrapperResult.optionalString)
                assertEquals(UNDEFINED_LOCALE, undefinedWrapperResult.optionalULocale)
                assertEquals(UNDEFINED_UUID, undefinedWrapperResult.optionalUUID)
                assertEquals(0, undefinedWrapperResult.optionalUUIDList?.size)

                val defaultWrapper = OptionalWrapperInput()
                val defaultWrapperQuery = OptionalScalarQuery(variables = OptionalScalarQuery.Variables(
                    optional = OptionalInput.Defined(defaultWrapper)
                ))
                val defaultResult = client.execute(defaultWrapperQuery).data?.optionalScalarQuery
                assertNotNull(defaultResult)
                assertEquals(UNDEFINED_BOOLEAN, defaultResult.optionalBoolean)
                assertEquals(UNDEFINED_DOUBLE, defaultResult.optionalDouble)
                assertEquals(UNDEFINED_STRING, defaultResult.optionalId)
                assertEquals(UNDEFINED_INT, defaultResult.optionalInt)
                assertEquals(0, defaultResult.optionalIntList?.size)
                assertEquals(UNDEFINED_OBJECT.foo, defaultResult.optionalObject?.foo)
                assertEquals(UNDEFINED_STRING, defaultResult.optionalString)
                assertEquals(UNDEFINED_LOCALE, defaultResult.optionalULocale)
                assertEquals(UNDEFINED_UUID, defaultResult.optionalUUID)
                assertEquals(0, defaultResult.optionalUUIDList?.size)
            }
        } finally {
            engine.stop(1000, 1000)
        }
    }

    @Test
    fun `verify null optionals are correctly serialized and deserialized`() {
        val engine = embeddedServer(CIO, port = 8080, module = Application::graphQLModule)
        try {
            engine.start()
            runBlocking {
                val client = GraphQLKtorClient(url = URL("http://localhost:8080/graphql"))

                val nullWrapperQuery = OptionalScalarQuery(variables = OptionalScalarQuery.Variables(optional = OptionalInput.Defined(null)))
                val nullWrapperResult = client.execute(nullWrapperQuery)
                assertNull(nullWrapperResult.data?.optionalScalarQuery)

                val nullWrapper = OptionalWrapperInput(
                    optionalBoolean = OptionalInput.Defined(null),
                    optionalDouble = OptionalInput.Defined(null),
                    optionalId = OptionalInput.Defined(null),
                    optionalInt = OptionalInput.Defined(null),
                    optionalIntList = OptionalInput.Defined(null),
                    optionalObject = OptionalInput.Defined(null),
                    optionalString = OptionalInput.Defined(null),
                    optionalULocale = OptionalInput.Defined(null),
                    optionalUUID = OptionalInput.Defined(null),
                    optionalUUIDList = OptionalInput.Defined(null)
                )
                val nullWrapperValuesQuery = OptionalScalarQuery(variables = OptionalScalarQuery.Variables(
                    optional = OptionalInput.Defined(nullWrapper)
                ))
                val nullResult = client.execute(nullWrapperValuesQuery).data?.optionalScalarQuery
                assertNotNull(nullResult)
                assertNull(nullResult.optionalBoolean)
                assertNull(nullResult.optionalDouble)
                assertNull(nullResult.optionalId)
                assertNull(nullResult.optionalInt)
                assertNull(nullResult.optionalIntList)
                assertNull(nullResult.optionalObject)
                assertNull(nullResult.optionalString)
                assertNull(nullResult.optionalULocale)
                assertNull(nullResult.optionalUUID)
                assertNull(nullResult.optionalUUIDList)
            }
        } finally {
            engine.stop(1000, 1000)
        }
    }

    @Test
    fun `verify defined optionals are correctly serialized and deserialized`() {
        val engine = embeddedServer(CIO, port = 8080, module = Application::graphQLModule)
        try {
            engine.start()
            runBlocking {
                val client = GraphQLKtorClient(url = URL("http://localhost:8080/graphql"))

                val randomUUID = UUID.randomUUID()
                val wrapper = OptionalWrapperInput(
                    optionalBoolean = OptionalInput.Defined(false),
                    optionalDouble = OptionalInput.Defined(-1.0),
                    optionalId = OptionalInput.Defined("id"),
                    optionalInt = OptionalInput.Defined(123),
                    optionalIntList = OptionalInput.Defined(listOf(123, 456)),
                    optionalObject = OptionalInput.Defined(SimpleInput(foo = "baz")),
                    optionalString = OptionalInput.Defined("defined"),
                    optionalULocale = OptionalInput.Defined(ULocale.FRANCE),
                    optionalUUID = OptionalInput.Defined(randomUUID),
                    optionalUUIDList = OptionalInput.Defined(listOf(randomUUID))
                )
                val wrapperQuery = OptionalScalarQuery(variables = OptionalScalarQuery.Variables(
                    optional = OptionalInput.Defined(wrapper)
                ))
                val result = client.execute(wrapperQuery).data?.optionalScalarQuery
                assertNotNull(result)
                assertEquals(false, result.optionalBoolean)
                assertEquals(-1.0, result.optionalDouble)
                assertEquals("id", result.optionalId)
                assertEquals(123, result.optionalInt)
                assertEquals(2, result.optionalIntList?.size)
                assertEquals(123, result.optionalIntList?.get(0))
                assertEquals(456, result.optionalIntList?.get(1))
                assertEquals("baz", result.optionalObject?.foo)
                assertEquals("defined", result.optionalString)
                assertEquals(ULocale.FRANCE, result.optionalULocale)
                assertEquals(randomUUID, result.optionalUUID)
                assertEquals(1, result.optionalUUIDList?.size)
                assertEquals(randomUUID, result.optionalUUIDList?.get(0))
            }
        } finally {
            engine.stop(1000, 1000)
        }
    }

    @Test
    fun `verify serialization and deserialization of required type`() {
        val engine = embeddedServer(CIO, port = 8080, module = Application::graphQLModule)
        try {
            engine.start()
            runBlocking {
                val client = GraphQLKtorClient(url = URL("http://localhost:8080/graphql"))

                val randomUUID = UUID.randomUUID()
                val wrapper = RequiredWrapperInput(
                    requiredBoolean = false,
                    requiredDouble = -1.0,
                    requiredId = "id",
                    requiredInt = 123,
                    requiredIntList = listOf(123, 456),
                    requiredObject = SimpleInput(foo = "baz"),
                    requiredString = "defined",
                    requiredULocale = ULocale.FRANCE,
                    requiredUUID = randomUUID,
                    requiredUUIDList = listOf(randomUUID)
                )
                val wrapperQuery = RequiredScalarQuery(variables = RequiredScalarQuery.Variables(
                    required = wrapper
                ))
                val result = client.execute(wrapperQuery).data?.scalarQuery
                assertNotNull(result)
                assertEquals(wrapper.requiredBoolean, result.requiredBoolean)
                assertEquals(wrapper.requiredDouble, result.requiredDouble)
                assertEquals(wrapper.requiredId, result.requiredId)
                assertEquals(wrapper.requiredInt, result.requiredInt)
                assertEquals(wrapper.requiredIntList.size, result.requiredIntList.size)
                assertEquals(wrapper.requiredIntList[0], result.requiredIntList[0])
                assertEquals(wrapper.requiredIntList[1], result.requiredIntList[1])
                assertEquals(wrapper.requiredObject.foo, result.requiredObject.foo)
                assertEquals(wrapper.requiredString, result.requiredString)
                assertEquals(wrapper.requiredULocale, result.requiredULocale)
                assertEquals(wrapper.requiredUUID, result.requiredUUID)
                assertEquals(wrapper.requiredUUIDList.size, result.requiredUUIDList.size)
                assertEquals(wrapper.requiredUUIDList[0], result.requiredUUIDList[0])
            }
        } finally {
            engine.stop(1000, 1000)
        }
    }
}
