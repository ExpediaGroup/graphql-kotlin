/*
 * Copyright 2022 Expedia, Inc
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
        // could not create persistedQuery extension
        null
    }

fun ExecutionInput.getQueryId(): String =
    String.format(
        "%064x",
        BigInteger(1, MESSAGE_DIGEST.digest(this.query.toByteArray(StandardCharsets.UTF_8)))
    ).also {
        MESSAGE_DIGEST.reset()
    }

fun ExecutionInput.isAutomaticPersistedQueriesExtensionInvalid(
    extension: AutomaticPersistedQueriesExtension
): Boolean =
    !this.getQueryId().equals(extension.sha256Hash, ignoreCase = true)
