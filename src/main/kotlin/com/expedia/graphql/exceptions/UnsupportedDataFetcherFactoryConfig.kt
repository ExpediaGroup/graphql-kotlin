package com.expedia.graphql.exceptions

import com.expedia.graphql.execution.DataFetcherFactoryConfig

class UnsupportedDataFetcherFactoryConfig(config: DataFetcherFactoryConfig): GraphQLKotlinException("KotlinDataFetcherFactoryProvider does not support specified config=$config")