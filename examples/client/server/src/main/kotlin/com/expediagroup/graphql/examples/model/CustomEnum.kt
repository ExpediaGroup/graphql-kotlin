package com.expediagroup.graphql.examples.model

import com.expediagroup.graphql.annotations.GraphQLDescription

@GraphQLDescription("Custom enum description")
enum class CustomEnum {
    @GraphQLDescription("First enum value")
    ONE,
    @GraphQLDescription("Second enum value")
    TWO,
    @GraphQLDescription("Third enum value")
    @Deprecated(message = "only goes up to two")
    THREE
}
