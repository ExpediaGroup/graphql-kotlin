package com.expedia.graphql.sample.context

import graphql.servlet.GraphQLContext
import graphql.servlet.GraphQLContextBuilder
import javax.servlet.http.HttpServletRequest
import javax.websocket.server.HandshakeRequest

/**
 * Basic [GraphQLContextBuilder] that creates custom [MyGraphQLContext].
 */
class MyGraphQLContextBuilder: GraphQLContextBuilder {

    override fun build(httpServletRequest: HttpServletRequest): GraphQLContext {
        val myValue = httpServletRequest.getHeader("MyHeader") ?: "context_from_servlet"
        return MyGraphQLContext(myValue, httpServletRequest = httpServletRequest)
    }

    override fun build(handshakeRequest: HandshakeRequest): GraphQLContext =
            MyGraphQLContext("context_from_handshake", handshakeRequest = handshakeRequest)

    override fun build(): GraphQLContext = MyGraphQLContext("default_context")
}
