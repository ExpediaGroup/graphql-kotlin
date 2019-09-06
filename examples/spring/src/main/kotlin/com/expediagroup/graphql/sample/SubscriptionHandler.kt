package com.expediagroup.graphql.sample

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import graphql.ExecutionResult
import graphql.GraphQL
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

class SubscriptionHandler(private val graphQL: GraphQL) : WebSocketHandler {

    private val objectMapper = ObjectMapper().registerKotlinModule()
    private val logger = LoggerFactory.getLogger(SubscriptionHandler::class.java)

    override fun handle(session: WebSocketSession): Mono<Void> {

        // This will not work with Apollo Client. There needs to be special logic to handle the "graphql-ws"
        // sub-protocol. That will be up to the server implementation to handle though.
        //
        // See: https://github.com/ExpediaGroup/graphql-kotlin/issues/155
        return session.send(session.receive()
            .doOnSubscribe {
                logger.info("Session starting. ID ${session.id}")
            }
            .doOnCancel {
                logger.info("Closing session: ID ${session.id}")
            }
            .concatMap {
                val graphQLRequest = objectMapper.readValue<GraphQLRequest>(it.payloadAsText)
                val executionInput = graphQLRequest.toExecutionInput()
                val executionResult = graphQL.execute(executionInput)
                executionResult.getData<Publisher<ExecutionResult>>()
            }
            .map { objectMapper.writeValueAsString(it.toGraphQLResponse()) }
            .map { session.textMessage(it) }
        )
    }
}
