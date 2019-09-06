package com.expediagroup.graphql.sample.mutation

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.sample.model.Widget
import org.springframework.stereotype.Component

/**
 * Simple widget mutation that shows that same objects can be used for input and output GraphQL types.
 */
@Component
class WidgetMutation : Mutation {

    @GraphQLDescription("modifies passed in widget so it doesn't have null value")
    fun processWidget(@GraphQLDescription("widget to be modified") widget: Widget): Widget {
        if (null == widget.value) {
            widget.value = 42
        }
        return widget
    }
}
