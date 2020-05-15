package com.expediagroup.graphql.examples.model

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.types.ID
import java.util.UUID

@GraphQLDescription("Wrapper that holds all supported scalar types")
data class ScalarWrapper(
    @GraphQLDescription("ID represents unique identifier that is not intended to be human readable")
    val id: ID,
    @GraphQLDescription("UTF-8 character sequence")
    val name: String,
    @GraphQLDescription("Either true or false")
    val valid: Boolean,
    @GraphQLDescription("A signed 32-bit nullable integer value")
    val count: Int?,
    @GraphQLDescription("A nullable signed double-precision floating-point value")
    val rating: Float?,
    @GraphQLDescription("Custom scalar")
    val custom: UUID
)
