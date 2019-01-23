package com.expedia.graphql.sample.dataFetchers

import com.expedia.graphql.execution.DataFetcherFactoryConfig
import com.expedia.graphql.execution.DataFetcherPropertyConfig
import com.expedia.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import graphql.schema.DataFetcherFactory

class CustomDataFetcherFactoryProvider(
        private val springDataFetcherFactory: SpringDataFetcherFactory,
        hooks: SchemaGeneratorHooks
) : KotlinDataFetcherFactoryProvider(hooks) {

    override fun getDataFetcherFactory(config: DataFetcherFactoryConfig): DataFetcherFactory<Any> =
            if (config is DataFetcherPropertyConfig && config.kProperty.isLateinit) {
                springDataFetcherFactory
            } else {
                super.getDataFetcherFactory(config)
            }
}