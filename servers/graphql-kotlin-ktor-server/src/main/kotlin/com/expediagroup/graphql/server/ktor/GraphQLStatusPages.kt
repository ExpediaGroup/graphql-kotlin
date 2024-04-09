package com.expediagroup.graphql.server.ktor

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

fun StatusPagesConfig.defaultGraphQLStatusPages(): StatusPagesConfig {
    exception<Throwable> { call, cause ->
        when (cause) {
            is UnsupportedOperationException -> call.respond(HttpStatusCode.MethodNotAllowed)
            else -> call.respond(HttpStatusCode.BadRequest) } }
    return this
}
