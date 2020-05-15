package com.expediagroup.graphql.examples.model

import com.expediagroup.graphql.annotations.GraphQLDescription

@GraphQLDescription("Very basic union of BasicObject and ComplexObject")
interface BasicUnion

@GraphQLDescription("Some basic description")
data class BasicObject(val id: Int, @GraphQLDescription("Object name") val name: String) : BasicUnion

@GraphQLDescription("""Multi line description of a complex type.
This is a second line of the paragraph.
This is final line of the description.""")
data class ComplexObject(
    @GraphQLDescription("Some unique identifier")
    val id: Int,
    @GraphQLDescription("Some object name")
    val name: String,
    @GraphQLDescription("""Optional value
Second line of the description""")
    val optional: String? = null,
    @GraphQLDescription("Some additional details")
    val details: DetailsObject
) : BasicUnion

@GraphQLDescription("Inner type object description")
data class DetailsObject(
    @GraphQLDescription("Unique identifier")
    val id: Int,
    @GraphQLDescription("Boolean flag")
    val flag: Boolean,
    @GraphQLDescription("Actual detail value")
    val value: String
)
