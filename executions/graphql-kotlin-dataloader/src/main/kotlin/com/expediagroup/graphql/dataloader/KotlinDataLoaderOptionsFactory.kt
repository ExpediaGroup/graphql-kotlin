package com.expediagroup.graphql.dataloader

import org.dataloader.DataLoaderOptions

open class KotlinDataLoaderOptionsFactory {
    open fun generate(contextMap: Map<*, Any>): DataLoaderOptions = DataLoaderOptions.newOptions()
}
