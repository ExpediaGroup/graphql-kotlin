package com.expediagroup.graphql.server.ktor

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

/**
 * Configures default exception handling using Ktor Status Pages.
 *
 * Returns following HTTP status codes:
 * * 405 (Method Not Allowed) - when attempting to execute mutation or query through a GET request
 * * 400 (Bad Request) - any other exception
 */
fun StatusPagesConfig.defaultGraphQLStatusPages(): StatusPagesConfig {
    exception<Throwable> { call, cause ->
        when (cause) {
            is UnsupportedOperationException -> call.respond(HttpStatusCode.MethodNotAllowed)
            else -> call.respond(HttpStatusCode.BadRequest) } }
    return this
}
