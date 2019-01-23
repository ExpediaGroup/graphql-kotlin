package com.expedia.graphql.execution

import com.expedia.graphql.exceptions.UnsupportedDataFetcherFactoryConfig
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.DataFetcherFactories
import graphql.schema.DataFetcherFactory
import graphql.schema.PropertyDataFetcher

open class KotlinDataFetcherFactoryProvider(private val hooks: SchemaGeneratorHooks) {

    private val defaultObjectMapper = jacksonObjectMapper()

    open fun getDataFetcherFactory(config: DataFetcherFactoryConfig): DataFetcherFactory<Any> = when (config) {
        is DataFetcherFunctionConfig -> DataFetcherFactories.useDataFetcher(
                FunctionDataFetcher(
                        target = config.target,
                        fn = config.kFunction,
                        objectMapper = defaultObjectMapper,
                        executionPredicate = hooks.dataFetcherExecutionPredicate))
        is DataFetcherPropertyConfig -> DataFetcherFactories.useDataFetcher(PropertyDataFetcher(config.kProperty.name))
        else -> throw UnsupportedDataFetcherFactoryConfig(config)
    }
}
