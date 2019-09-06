package com.expediagroup.graphql.sample

import com.expediagroup.graphql.sample.context.MyGraphQLContext
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
        }
}
