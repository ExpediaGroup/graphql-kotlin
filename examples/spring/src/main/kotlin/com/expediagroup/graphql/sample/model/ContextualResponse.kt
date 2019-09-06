package com.expediagroup.graphql.sample.model

import com.expediagroup.graphql.annotations.GraphQLDescription

@GraphQLDescription("simple response that contains value read from context")
data class ContextualResponse(val passedInValue: Int, val contextValue: String)
