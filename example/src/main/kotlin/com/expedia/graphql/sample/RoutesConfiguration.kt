package com.expedia.graphql.sample

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.TypeFactory
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.html
import org.springframework.web.reactive.function.server.json
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class RoutesConfiguration(
    val schema: GraphQLSchema,
    val schemaPrinter: SchemaPrinter,
    private val queryHandler: QueryHandler,
    private val objectMapper: ObjectMapper,
    @Value("classpath:/graphql-playground.html") private val playgroundHtml: Resource
) {

    private val mapTypeReference: MapType = TypeFactory.defaultInstance().constructMapType(HashMap::class.java, String::class.java, Any::class.java)

    @Bean
    fun graphQLRoutes() = router {
        (POST("/graphql") or GET("/graphql")).invoke { serverRequest ->
            createGraphQLRequest(serverRequest)
                .flatMap { graphQLRequest -> queryHandler.executeQuery(graphQLRequest) }
                .flatMap { result -> ok().json().syncBody(result) }
                .switchIfEmpty(badRequest().build())
        }
    }

    @Bean
    fun sdlRoute() = router {
        GET("/sdl") {
            ok().contentType(MediaType.TEXT_PLAIN).syncBody(schemaPrinter.print(schema))
        }
    }

    @Bean
    fun graphQLToolRoutes() = router {
        GET("/playground") {
            ok().html().syncBody(playgroundHtml)
        }
    }

    private fun createGraphQLRequest(serverRequest: ServerRequest): Mono<GraphQLRequest> = when {
        serverRequest.method() == HttpMethod.POST -> serverRequest.bodyToMono()
        serverRequest.queryParam("query").isPresent -> {
            val query = serverRequest.queryParam("query").get()
            val operationName: String? = serverRequest.queryParam("operationName").orElseGet { null }
            val variables: String? = serverRequest.queryParam("variables").orElseGet { null }
            val graphQLVariables: Map<String, Any>? = variables?.let {
                objectMapper.readValue(it, mapTypeReference)
            }
            Mono.just(GraphQLRequest(query = query, operationName = operationName, variables = graphQLVariables))
        }
        else -> Mono.empty()
    }
}
