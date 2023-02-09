package com.example

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class HelloWorldQuery : Query {

    fun helloWorld(name: String? = null) = if (name != null) {
        "Hello, $name!!!"
    } else {
        "Hello, World!!!"
    }

    fun randomUUID(): UUID = UUID.randomUUID()
}
