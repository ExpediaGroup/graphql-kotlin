package com.expediagroup.graphql.client.serializer

import com.expediagroup.graphql.client.types.GraphQLClientResponse
import java.util.ServiceLoader
import kotlin.reflect.KClass

interface GraphQLClientSerializer {

    fun serialize(request: Any): String

    fun <T : Any> deserialize(rawResponse: String, responseType: KClass<T>): GraphQLClientResponse<T>

    fun deserialize(rawResponses: String, responseTypes: List<KClass<*>>): List<GraphQLClientResponse<*>>
}

fun defaultGraphQLSerializer(): GraphQLClientSerializer = ServiceLoader.load(GraphQLClientSerializer::class.java)
    .firstOrNull() ?: throw MissingDefaultGraphQLClientSerializerException

object MissingDefaultGraphQLClientSerializerException : IllegalStateException(
    "Unable to find default GraphQL Kotlin client deserializer. Verify graphql-kotlin-client-jackson or graphql-kotlin-client-serialization is available on the classpath"
)
