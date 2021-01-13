package com.example

import com.expediagroup.graphql.types.operations.Query
import org.springframework.stereotype.Component

@Component
class SimpleQuery : Query {

    fun helloWorld(name: String? = null) = if (name != null) {
        "Hello, $name!!!"
    } else {
        "Hello, World!!!"
    }
}
