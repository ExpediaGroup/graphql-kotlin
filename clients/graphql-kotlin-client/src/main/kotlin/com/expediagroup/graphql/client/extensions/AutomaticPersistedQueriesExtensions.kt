package com.expediagroup.graphql.client.extensions

import com.expediagroup.graphql.client.types.AutomaticPersistedQueriesExtension
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

internal val MESSAGE_DIGEST: MessageDigest = MessageDigest.getInstance("SHA-256")

fun GraphQLClientRequest<*>.getQueryId(): String =
    String.format(
        "%064x",
        BigInteger(1, MESSAGE_DIGEST.digest(this.query?.toByteArray(StandardCharsets.UTF_8)))
    ).also {
        MESSAGE_DIGEST.reset()
    }

fun AutomaticPersistedQueriesExtension.toQueryParamString() = """{"persistedQuery":{"version":$version,"sha256Hash":"$sha256Hash"}}"""
fun AutomaticPersistedQueriesExtension.toExtentionsBodyMap() = mapOf(
    "persistedQuery" to mapOf(
        "version" to version,
        "sha256Hash" to sha256Hash
    )
)
