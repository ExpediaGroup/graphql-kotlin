package com.expediagroup.graphql.plugin

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class DownloadSchemaTest {

    @BeforeEach
    fun setUp() {
        WireMock.reset()
    }

    @Test
    @KtorExperimentalAPI
    fun `verify can download SDL`() {
        val expectedSchema = """
            schema {
              query: Query
            }

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Directs the executor to skip this field or fragment when the `if`'argument is true."
            directive @skip(
                "Skipped when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Marks the field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String! = "No longer supported"
              ) on FIELD_DEFINITION | ENUM_VALUE

            type Query {
              widget: Widget!
            }

            type Widget {
              id: Int!
              name: String!
            }""".trimIndent()
        stubFor(get("/sdl")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withBody(expectedSchema)))

        runBlocking {
            val sdl = downloadSchema("${wireMockServer.baseUrl()}/sdl")
            assertEquals(expectedSchema, sdl)
        }
    }

    @Test
    @KtorExperimentalAPI
    fun `verify downloadSchema will throw exception if URL is not valid`() {
        assertThrows<RuntimeException> {
            runBlocking {
                downloadSchema("http://non-existent-graphql-url.com")
            }
        }
    }

    @Test
    @KtorExperimentalAPI
    fun `verify downloadSchema will throw exception if downloaded SDL is not valid schema`() {
        stubFor(get("whatever")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withBody("some random body")))
        assertThrows<RuntimeException> {
            runBlocking {
                downloadSchema(endpoint = "${wireMockServer.baseUrl()}/whatever")
            }
        }
    }

    @Test
    @KtorExperimentalAPI
    fun `verify downloadSchema will throw exception if unable to download schema`() {
        stubFor(get("sdl")
            .willReturn(aResponse()
                .withStatus(404)))
        assertThrows<RuntimeException> {
            runBlocking {
                downloadSchema("${wireMockServer.baseUrl()}/sdl")
            }
        }
    }

    companion object {
        private val wireMockServer: WireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())

        @BeforeAll
        @JvmStatic
        fun oneTimeSetup() {
            wireMockServer.start()
            WireMock.configureFor(wireMockServer.port())
        }

        @AfterAll
        @JvmStatic
        fun oneTimeTearDown() {
            wireMockServer.stop()
        }
    }
}
