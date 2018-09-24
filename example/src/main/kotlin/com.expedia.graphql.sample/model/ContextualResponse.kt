package com.expedia.graphql.sample.model

import com.expedia.graphql.annotations.GraphQLDescription

@GraphQLDescription("simple response that contains value read from context")
data class ContextualResponse(val passedInValue: Int, val contextValue: String)