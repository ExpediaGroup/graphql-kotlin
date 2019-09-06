package com.expediagroup.graphql.sample.query

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class EnvironmentQuery : Query {
    fun nestedEnvironment(value: Int) = NestedNode(value)
}

data class NestedNode(
    val value: Int,
    val parentValue: Int? = null
) {
    fun nested(environment: DataFetchingEnvironment, value: Int): NestedNode {
        val parentValue: Int? = if (environment.executionStepInfo.hasParent()) {
            environment.executionStepInfo.parent.getArgument("value")
        } else {
            null
        }

        return NestedNode(value, parentValue)
    }
}
