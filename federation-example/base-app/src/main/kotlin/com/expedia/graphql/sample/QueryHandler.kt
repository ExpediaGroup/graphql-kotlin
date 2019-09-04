package com.expedia.graphql.sample

import com.expedia.graphql.sample.context.MyGraphQLContext
import com.expedia.graphql.sample.exceptions.SimpleKotlinGraphQLError
import graphql.ErrorType
import graphql.GraphQL
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class QueryHandler(private val graphql: GraphQL) {

    fun executeQuery(request: GraphQLRequest): Mono<GraphQLResponse> = Mono.subscriberContext()
        .flatMap { ctx ->
            val graphQLContext: MyGraphQLContext = ctx.get("graphQLContext")
            val input = request.toExecutionInput(graphQLContext)

            Mono.fromFuture(graphql.executeAsync(input))
                .map { executionResult -> executionResult.toGraphQLResponse() }
        }.onErrorResume {
            val graphQLError = SimpleKotlinGraphQLError(it, ErrorType.DataFetchingException)

            Mono.just(GraphQLResponse(errors = listOf(graphQLError)))
        }
}
