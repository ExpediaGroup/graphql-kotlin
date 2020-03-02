package com.expediagroup.graphql.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import java.net.URL

@KtorExperimentalAPI
class GraphQLClient(private val url: URL, engine: HttpClientEngineFactory<*> = CIO, vararg features: HttpClientFeature<*, *>) {

    private val client = HttpClient(engineFactory = engine) {
        for (feature in features) {
            install(feature)
        }
        // install default serializer
        if (features.none { "json".equals(it.key.name, ignoreCase = true) }) {
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
        }
    }

    suspend fun <T> executeOperation(query: String, operationName: String? = null, variables: Any? = null): GraphQLResult<T> {
        // variables are data classes
        // by using map instead of typed object we can eliminate the need to convert variables to map
        val graphQLRequest = mapOf(
            "query" to query,
            "operationName" to operationName,
            "variables" to variables
        )

        return client.post(url) {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = graphQLRequest
        }
    }
}
