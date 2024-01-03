package com.expediagroup.graphql.apq.extensions

import com.expediagroup.graphql.apq.provider.AutomaticPersistedQueriesExtension
import graphql.ExecutionInput
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

internal const val APQ_EXTENSION_KEY: String = "persistedQuery"

@Suppress("UNCHECKED_CAST")
fun ExecutionInput.getAutomaticPersistedQueriesExtension(): AutomaticPersistedQueriesExtension? =
    try {
        (this.extensions[APQ_EXTENSION_KEY] as? Map<String, Any?>)?.let(::AutomaticPersistedQueriesExtension)
    } catch (e: NoSuchElementException) {
        // could not create persistedQuery extension
        null
    }

fun ExecutionInput.getQueryId(): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    return String.format(
        "%064x",
        BigInteger(1, messageDigest.digest(this.query.toByteArray(StandardCharsets.UTF_8)))
    )
}

fun ExecutionInput.isAutomaticPersistedQueriesExtensionInvalid(
    extension: AutomaticPersistedQueriesExtension
): Boolean =
    !this.getQueryId().equals(extension.sha256Hash, ignoreCase = true)
