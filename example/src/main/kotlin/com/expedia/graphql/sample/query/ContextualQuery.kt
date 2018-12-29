package com.expedia.graphql.sample.query

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.sample.context.MyGraphQLContext
import com.expedia.graphql.sample.model.ContextualResponse
import org.springframework.stereotype.Component

/**
 * Example usage of [GraphQLContext] annotation. By using this annotation context parameter won't be exposed as in the
 * schema and will be automatically autowired at runtime using value from the environment.
 *
 * @see com.expedia.graphql.sample.context.MyGraphQLContextBuilder
 * @see com.expedia.graphql.KotlinDataFetcher
 */
@Component
class ContextualQuery: Query {

    @GraphQLDescription("query that uses GraphQLContext context")
    fun contextualQuery(
        @GraphQLDescription("some value that will be returned to the user")
        value: Int,
        @GraphQLContext context: MyGraphQLContext
    ): ContextualResponse = ContextualResponse(value, context.myCustomValue)
}
