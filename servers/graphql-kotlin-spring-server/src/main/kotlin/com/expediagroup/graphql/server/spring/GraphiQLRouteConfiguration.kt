package com.expediagroup.graphql.server.spring

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.html

/**
 * Configuration for exposing the GraphiQL on a specific HTTP path
 */
@ConditionalOnProperty(value = ["graphql.graphiql.enabled"], havingValue = "true", matchIfMissing = true)
@Configuration
class GraphiQLRouteConfiguration(
    private val config: GraphQLConfigurationProperties,
    @Value("classpath:/graphql-graphiql.html") private val html: Resource,
    @Value("\${spring.webflux.base-path:#{null}}") private val contextPath: String?
) {
    @Bean
    fun graphiqlRoute(): RouterFunction<ServerResponse> = coRouter {
        GET(config.graphiql.endpoint) {
            ok().html().bodyValueAndAwait(
                html.inputStream.bufferedReader().use { reader ->
                    reader.readText()
                        .replace("\${graphQLEndpoint}", if (contextPath.isNullOrBlank()) config.endpoint else "$contextPath/${config.endpoint}")
                        .replace("\${subscriptionsEndpoint}", if (contextPath.isNullOrBlank()) config.subscriptions.endpoint else "$contextPath/${config.subscriptions.endpoint}")
                }
            )
        }
    }
}
