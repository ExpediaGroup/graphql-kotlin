package com.expediagroup.graphql.generator.execution

import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.LightDataFetcher
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Singleton Property [DataFetcher] that stores references to underlying properties getters.
 */
internal object SingletonPropertyDataFetcher : LightDataFetcher<Any?> {

    private val factory: DataFetcherFactory<Any?> = DataFetcherFactory<Any?> { SingletonPropertyDataFetcher }

    private val getters: ConcurrentHashMap<String, KProperty.Getter<*>> = ConcurrentHashMap()

    fun getFactoryAndRegister(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any?> {
        getters.computeIfAbsent("${kClass.java.name}.${kProperty.name}") {
            kProperty.getter
        }
        return factory
    }

    override fun get(
        fieldDefinition: GraphQLFieldDefinition,
        sourceObject: Any?,
        environmentSupplier: Supplier<DataFetchingEnvironment>
    ): Any? =
        sourceObject?.let {
            getters["${sourceObject.javaClass.name}.${fieldDefinition.name}"]?.call(sourceObject)
        }

    override fun get(environment: DataFetchingEnvironment): Any? =
        environment.getSource<Any?>()?.let { sourceObject ->
            getters["${sourceObject.javaClass.name}.${environment.fieldDefinition.name}"]?.call(sourceObject)
        }
}
