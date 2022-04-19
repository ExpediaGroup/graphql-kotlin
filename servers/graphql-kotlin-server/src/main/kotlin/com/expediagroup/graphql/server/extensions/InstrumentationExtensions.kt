package com.expediagroup.graphql.server.extensions

import com.expediagroup.graphql.dataloader.instrumentation.level.DataLoaderLevelDispatchedInstrumentation
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.DataLoaderSyncExecutionExhaustedInstrumentation
import graphql.execution.instrumentation.Instrumentation

internal fun Instrumentation.isDataLoaderInstrumentation(): Boolean =
    javaClass == DataLoaderLevelDispatchedInstrumentation::class.java ||
        javaClass == DataLoaderSyncExecutionExhaustedInstrumentation::class.java
