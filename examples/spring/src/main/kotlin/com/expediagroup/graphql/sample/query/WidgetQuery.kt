package com.expediagroup.graphql.sample.query

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.sample.model.Widget
import org.springframework.stereotype.Component

/**
 * Simple widget query.
 */
@Component
class WidgetQuery: Query {

    @GraphQLDescription("creates new widget for given ID")
    fun widgetById(@GraphQLDescription("The special ingredient") id: Int): Widget? = Widget(id)
}
