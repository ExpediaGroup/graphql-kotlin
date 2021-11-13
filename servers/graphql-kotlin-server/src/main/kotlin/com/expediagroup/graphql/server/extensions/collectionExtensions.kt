package com.expediagroup.graphql.server.extensions

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

suspend fun <A, B> Iterable<A>.concurrentMap(
    transform: suspend (A) -> B,
    fallback: (A, exception: Exception) -> B
): List<B> =
    supervisorScope {
        map { item ->
            async {
                try {
                    transform(item)
                } catch (e: Exception) {
                    fallback(item, e)
                }
            }
        }.awaitAll()
    }

