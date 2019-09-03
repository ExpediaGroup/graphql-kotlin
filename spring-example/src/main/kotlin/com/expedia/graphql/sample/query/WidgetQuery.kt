package com.expedia.graphql.sample.query

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.sample.model.Widget
import org.springframework.stereotype.Component

/**
 * Simple widget query.
 */
@Component
class WidgetQuery: Query {

    @GraphQLDescription("creates new widget for given ID")
    fun widgetById(@GraphQLDescription("The special ingredient") id: Int): Widget? = Widget(id)
}