package com.expediagroup.graphql.dataloader.instrumentation.extensions

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.instrumentation.exceptions.MissingInstrumentationStateException
import com.expediagroup.graphql.dataloader.instrumentation.exceptions.MissingKotlinDataLoaderRegistryException
import com.expediagroup.graphql.dataloader.instrumentation.level.state.ExecutionLevelDispatchedState
import com.expediagroup.graphql.dataloader.instrumentation.level.state.Level
import com.expediagroup.graphql.dataloader.instrumentation.syncexhaustion.state.SyncExecutionExhaustedState
import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoader
import java.util.concurrent.CompletableFuture

/**
 * Check if all futures collected on [KotlinDataLoaderRegistry.dispatchAll] were handled and we have more futures than we
 * had when we started to dispatch, if so, means that [DataLoader]s were chained
 */
internal fun <V> CompletableFuture<V>.dispatchIfNeeded(
    environment: DataFetchingEnvironment
): CompletableFuture<V> {
    val dataLoaderRegistry =
        environment
            .graphQlContext.get<KotlinDataLoaderRegistry>(KotlinDataLoaderRegistry::class)
            ?: throw MissingKotlinDataLoaderRegistryException()

    if (dataLoaderRegistry.dataLoadersInvokedOnDispatch()) {
        val cantContinueExecution = when {
            environment.graphQlContext.hasKey(ExecutionLevelDispatchedState::class) -> {
                environment
                    .graphQlContext.get<ExecutionLevelDispatchedState>(ExecutionLevelDispatchedState::class)
                    .allExecutionsDispatched(Level(environment.executionStepInfo.path.level))
            }
            environment.graphQlContext.hasKey(SyncExecutionExhaustedState::class) -> {
                environment
                    .graphQlContext.get<SyncExecutionExhaustedState>(SyncExecutionExhaustedState::class)
                    .allSyncExecutionsExhausted()
            }
            else -> throw MissingInstrumentationStateException()
        }

        if (cantContinueExecution) {
            dataLoaderRegistry.dispatchAll()
        }
    }
    return this
}
