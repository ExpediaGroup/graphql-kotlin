package com.expediagroup.graphql.transactionbatcher.transaction

import java.util.concurrent.CompletableFuture

data class BatcheableTransaction<TInput, TOutput>(
    val input: TInput,
    val future: CompletableFuture<TOutput>,
    val key: String
)
