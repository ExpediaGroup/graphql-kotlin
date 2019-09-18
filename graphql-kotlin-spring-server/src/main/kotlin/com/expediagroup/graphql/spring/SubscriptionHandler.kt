/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.spring

import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.model.toExecutionInput
import com.expediagroup.graphql.spring.model.toGraphQLResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.ExecutionResult
import graphql.GraphQL
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux

/**
 * WebSocket handler for handling GraphQL subscriptions.
 */
class SubscriptionHandler(
    private val graphQL: GraphQL,
    private val objectMapper: ObjectMapper
) : WebSocketHandler {

    private val logger = LoggerFactory.getLogger(SubscriptionHandler::class.java)

    @Suppress("ForbiddenVoid")
    override fun handle(session: WebSocketSession): Mono<Void> {
        val response = session.receive()
            .concatMap {
                val graphQLRequest = objectMapper.readValue<GraphQLRequest>(it.payloadAsText)
                val executionResult = graphQL.execute(graphQLRequest.toExecutionInput())
                executionResult.getData<Publisher<ExecutionResult>>()
                    .toFlux()
                    .doOnSubscribe { logger.debug("WebSocketSession subscribe, ID=${session.id}") }
                    .doOnCancel { logger.debug("WebSocketSession cancel, ID=${session.id}") }
                    .doOnComplete { logger.debug("WebSocketSession complete, ID=${session.id}") }
                    .doFinally { session.close() }
            }
            .map { objectMapper.writeValueAsString(it.toGraphQLResponse()) }
            .map { session.textMessage(it) }

        return session.send(response)
    }

    override fun getSubProtocols(): List<String> = listOf("graphql-ws")
}
