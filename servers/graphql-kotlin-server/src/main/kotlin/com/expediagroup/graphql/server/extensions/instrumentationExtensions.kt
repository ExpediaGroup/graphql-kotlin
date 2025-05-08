package com.expediagroup.graphql.server.extensions

import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.GraphQLSyncExecutionExhaustedDataLoaderDispatcher
import graphql.execution.instrumentation.Instrumentation

internal fun Instrumentation.isBatchDataLoaderInstrumentation(): Boolean =
    javaClass == GraphQLSyncExecutionExhaustedDataLoaderDispatcher::class.java
