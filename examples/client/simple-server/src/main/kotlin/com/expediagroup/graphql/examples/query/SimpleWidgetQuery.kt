package com.expediagroup.graphql.examples.query

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class SimpleWidgetQuery : Query {
    fun widget() = Widget(
        id = Random.nextInt(),
        name = "Example",
        type = WidgetType.values().random()
    )
}

@GraphQLDescription("Simple Widget")
data class Widget(
    @GraphQLDescription("Unique identifier")
    val id: Int,
    @GraphQLDescription("Name of the widget")
    val name: String,
    @GraphQLDescription("Type of the widget")
    val type: WidgetType
)

@GraphQLDescription("Supported widget types")
enum class WidgetType {
    @GraphQLDescription("CONTROL type widget")
    CONTROL,
    @GraphQLDescription("INFORMATION type widget")
    INFORMATION
}
