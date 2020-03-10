package com.expediagroup.graphql.plugin

import graphql.schema.idl.SchemaParser
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.util.KtorExperimentalAPI

/**
 * Downloads and verifies GraphQL schema in SDL format from the specified endpoint.
 */
@KtorExperimentalAPI
suspend fun downloadSchema(endpoint: String): String = HttpClient(CIO).use { client ->
    val sdl = try {
        client.get<String>(urlString = endpoint)
    } catch (e: Throwable) {
        throw RuntimeException("Unable to download SDL from specified endpoint=$endpoint", e)
    }
    SchemaParser().parse(sdl)
    sdl
}
