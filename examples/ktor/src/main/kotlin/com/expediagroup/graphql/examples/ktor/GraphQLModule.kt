package com.expediagroup.graphql.examples.ktor

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

fun Application.graphQLModule() {
    install(Routing)

    routing {
        get("hello") {
            this.call.respond("world")
        }

        post("graphql") {
            GraphQLHandler().handle(this.call)
        }
    }
}
