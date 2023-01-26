package com.expediagroup.ktor.kotlinx.queries

import com.expediagroup.graphql.server.operations.Query

class HelloWorld : Query {
    fun helloWorld(name: String? = null): String = if (name != null) {
        "Hello $name!"
    } else {
        "Hello World!"
    }
}
