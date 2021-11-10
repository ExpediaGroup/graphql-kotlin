package com.expediagroup.graphql.server.extensions

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

suspend fun <A, B> Iterable<A>.concurrentMap(transform: suspend (A) -> B): List<B> = supervisorScope {
    map { async { transform(it) } }.awaitAll()
}
