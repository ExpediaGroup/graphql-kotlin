package com.expediagroup.graphql.examples.query

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.examples.model.Widget
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

/**
 * Example query that showcases usage of `SpringDataFetcher`.
 */
@Component
class SpringAwareQuery : Query {

    @GraphQLDescription("retrieves Widget from the repository by ID")
    fun widgetById(
        @GraphQLIgnore @Autowired repository: WidgetRepository,
        @GraphQLDescription("The special ingredient") id: Int
    ): Widget? = repository.findWidget(id)

    @GraphQLDescription("retrieves all widgets from repository")
    fun availableWidgets(@GraphQLIgnore @Autowired repository: WidgetRepository): List<Widget> = repository.retrieveAllWidgets()
}

@Service
class WidgetRepository {

    private val widgets = mapOf(
        1 to Widget(value = 123),
        2 to Widget(value = 234)
    )

    fun findWidget(id: Int): Widget? = widgets[id]

    fun retrieveAllWidgets(): List<Widget> = widgets.values.toList()
}
