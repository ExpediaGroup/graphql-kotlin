package com.expediagroup.graphql.apq.extensions

import com.expediagroup.graphql.apq.provider.AutomaticPersistedQueriesExtension
import graphql.ExecutionInput
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

internal const val APQ_EXTENSION_KEY: String = "persistedQuery"
internal val MESSAGE_DIGEST: MessageDigest = MessageDigest.getInstance("SHA-256")

@Suppress("UNCHECKED_CAST")
fun ExecutionInput.getAutomaticPersistedQueriesExtension(): AutomaticPersistedQueriesExtension? =
    try {
        (this.extensions[APQ_EXTENSION_KEY] as? Map<String, Any?>)?.let(::AutomaticPersistedQueriesExtension)
    } catch (e: NoSuchElementException) {
        // could not creat persistedQuery extension
        null
    }

fun ExecutionInput.isAutomaticPersistedQueriesExtensionInvalid(
    extension: AutomaticPersistedQueriesExtension
): Boolean {
    val bigInteger = BigInteger(1, MESSAGE_DIGEST.digest(this.query.toByteArray(StandardCharsets.UTF_8)))
    val calculatedPersistedQueryId = String.format("%064x", bigInteger)
    return !calculatedPersistedQueryId.equals(extension.sha256Hash, ignoreCase = true)
}
