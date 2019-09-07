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

package com.expediagroup.graphql.boot.server

import com.expediagroup.graphql.boot.server.model.GraphQLRequest
import com.expediagroup.graphql.boot.server.model.toExecutionInput
import com.expediagroup.graphql.boot.server.model.toGraphQLResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.ExecutionResult
import graphql.GraphQL
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

class SubscriptionHandler(
    private val graphQL: GraphQL,
    private val objectMapper: ObjectMapper
) : WebSocketHandler {

    private val logger = LoggerFactory.getLogger(SubscriptionHandler::class.java)

    @Suppress("ForbiddenVoid")
    override fun handle(session: WebSocketSession): Mono<Void> = session.send(session.receive()
            .doOnSubscribe {
                logger.debug("session starting, ID=${session.id}")
            }
            .doOnCancel {
                logger.debug("closing session, ID=${session.id}")
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

    override fun getSubProtocols(): List<String> = listOf("graphql-ws")
}
