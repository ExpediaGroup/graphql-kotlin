package com.expediagroup.graphql.examples.federation.reviews.query

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

/**
 * Provides a simple dummy query to ensure the schema has a root field.
 */
@Component
class ReviewsQuery : Query {
    fun dummyQuery(): String = "This is a dummy query for the reviews subgraph"
}
