package com.expediagroup.polymorphic

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.graphQLModule() {
    val jacksonObjectMapper: ObjectMapper = jacksonObjectMapper()
    val ktorGraphQLServer: KtorGraphQLServer = KtorGraphQLServer(jacksonObjectMapper)

    install(Routing)
    routing {
        post("graphql") {
            val result = ktorGraphQLServer.execute(call.request)
            if (result != null) {
                val json = jacksonObjectMapper.writeValueAsString(result)
                call.response.call.respond(json)
            } else {
                call.response.call.respond(HttpStatusCode.BadRequest, "Invalid request")
            }
        }
        get("playground") {
            this.call.respondText(buildPlaygroundHtml("graphql", "subscriptions"), ContentType.Text.Html)
        }
    }
}

private fun buildPlaygroundHtml(graphQLEndpoint: String, subscriptionsEndpoint: String) =
    Application::class.java.classLoader.getResource("graphql-playground.html")?.readText()
        ?.replace("\${graphQLEndpoint}", graphQLEndpoint)
        ?.replace("\${subscriptionsEndpoint}", subscriptionsEndpoint)
        ?: throw IllegalStateException("graphql-playground.html cannot be found in the classpath")
