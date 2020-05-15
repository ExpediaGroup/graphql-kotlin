package com.expediagroup.graphql.examples.model

import com.expediagroup.graphql.annotations.GraphQLDescription

@GraphQLDescription("Test input object")
data class SimpleArgument(
    @GraphQLDescription("New value to be set")
    val newName: String?,
    @GraphQLDescription("Minimum value for test criteria")
    val min: Float?,
    @GraphQLDescription("Maximum value for test criteria")
    val max: Float?
)
