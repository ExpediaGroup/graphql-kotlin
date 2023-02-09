package com.expediagroup.webclient

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class HelloWorld : Query {
    fun helloWorld(name: String? = null): String = if (name != null) {
        "Hello $name!"
    } else {
        "Hello World!"
    }
}
