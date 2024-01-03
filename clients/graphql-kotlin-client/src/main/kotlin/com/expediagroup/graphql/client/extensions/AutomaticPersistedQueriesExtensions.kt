package com.expediagroup.graphql.client.extensions

import com.expediagroup.graphql.client.types.AutomaticPersistedQueriesExtension
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun GraphQLClientRequest<*>.getQueryId(): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    return String.format(
        "%064x",
        BigInteger(1, messageDigest.digest(this.query?.toByteArray(StandardCharsets.UTF_8)))
    )
}

fun AutomaticPersistedQueriesExtension.toQueryParamString(): String =
    """{"persistedQuery":{"version":$version,"sha256Hash":"$sha256Hash"}}"""

fun AutomaticPersistedQueriesExtension.toExtensionsBodyMap(): Map<String, Map<String, Any>> = mapOf(
    "persistedQuery" to mapOf(
        "version" to version,
        "sha256Hash" to sha256Hash
    )
)
