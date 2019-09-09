/*
 * Copyright 2019 Expedia Group
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
