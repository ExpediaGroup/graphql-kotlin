package com.expedia.graphql.sample.datafetchers

import com.expedia.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import graphql.schema.DataFetcherFactory
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Custom DataFetcherFactory provider that returns custom Spring based DataFetcherFactory for resolving lateinit properties.
 */
class CustomDataFetcherFactoryProvider(
    private val springDataFetcherFactory: SpringDataFetcherFactory,
    hooks: SchemaGeneratorHooks
) : KotlinDataFetcherFactoryProvider(hooks) {

    override fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any> =
        if (kProperty.isLateinit) {
            springDataFetcherFactory
        } else {
            super.propertyDataFetcherFactory(kClass, kProperty)
        }
}
