package com.expediagroup.graphql.examples.model

import com.expediagroup.graphql.annotations.GraphQLDescription

@GraphQLDescription("Example of an object self-referencing itself")
data class NestedObject(
    @GraphQLDescription("Unique identifier")
    val id: Int,
    @GraphQLDescription("Name of the object")
    val name: String,
    @GraphQLDescription("Children elements")
    val children: List<NestedObject>
)
