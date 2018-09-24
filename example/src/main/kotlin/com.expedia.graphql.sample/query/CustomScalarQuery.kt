package com.expedia.graphql.sample.query

import com.expedia.graphql.annotations.GraphQLDescription
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Simple query that exposes custom scalar.
 */
@Component
class CustomScalarQuery: Query {

    @GraphQLDescription("generates random UUID")
    fun generateRandomUUID() = UUID.randomUUID()
}