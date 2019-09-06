package com.expediagroup.graphql.execution

import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.DataFetcherFactories
import graphql.schema.DataFetcherFactory
import graphql.schema.PropertyDataFetcher
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

/**
 * DataFetcherFactoryProvider is used during schema construction to obtain [DataFetcherFactory] that should be used
 * for target function and property resolution.
 */
open class KotlinDataFetcherFactoryProvider(private val hooks: SchemaGeneratorHooks) {

    private val defaultObjectMapper = jacksonObjectMapper()

    /**
     * Retrieve instance of [DataFetcherFactory] that will be used to resolve target function.
     *
     * @param target target object that performs the data fetching or NULL if target object should be dynamically
     * retrieved during data fetcher execution from [graphql.schema.DataFetchingEnvironment]
     * @param kFunction Kotlin function being invoked
     */
    open fun functionDataFetcherFactory(target: Any?, kFunction: KFunction<*>): DataFetcherFactory<Any> =
            DataFetcherFactories.useDataFetcher(
                FunctionDataFetcher(
                        target = target,
                        fn = kFunction,
                        objectMapper = defaultObjectMapper,
                        executionPredicate = hooks.dataFetcherExecutionPredicate))

    /**
     * Retrieve instance of [DataFetcherFactory] that will be used to resolve target property.
     *
     * @param kClass parent class that contains this property
     * @param kProperty Kotlin property that should be resolved
     */
    open fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any> = DataFetcherFactory<Any> {
        PropertyDataFetcher(kProperty.name)
    }
}
