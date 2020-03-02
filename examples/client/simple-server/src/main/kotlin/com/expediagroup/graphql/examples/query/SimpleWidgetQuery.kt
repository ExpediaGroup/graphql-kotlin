package com.expediagroup.graphql.examples.query

import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component

@Component
class SimpleWidgetQuery : Query {
    fun widget() = Widget(id = 1, name = "Example")
}

data class Widget(
    val id: Int,
    val name: String
)
