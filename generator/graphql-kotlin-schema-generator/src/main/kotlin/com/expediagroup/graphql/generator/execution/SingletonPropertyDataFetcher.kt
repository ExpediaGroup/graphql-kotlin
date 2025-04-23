package com.expediagroup.graphql.generator.execution

import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.LightDataFetcher
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

/**
 * Singleton Property [DataFetcher] that stores references to underlying properties getters.
 */
internal object SingletonPropertyDataFetcher : LightDataFetcher<Any?> {

    private val logger = LoggerFactory.getLogger(SingletonPropertyDataFetcher::class.java)
    val factory: DataFetcherFactory<Any?> = DataFetcherFactory<Any?> { SingletonPropertyDataFetcher }
    private val getters: ConcurrentHashMap<String, KProperty.Getter<*>> = ConcurrentHashMap()

    fun register(kClass: KClass<*>, kProperty: KProperty<*>) {
        getters.computeIfAbsent("${kClass.java.name}.${kProperty.name}") {
            kProperty.getter
        }
    }

    override fun get(
        fieldDefinition: GraphQLFieldDefinition,
        sourceObject: Any?,
        environmentSupplier: Supplier<DataFetchingEnvironment>
    ): Any? =
        sourceObject?.let {
            getters["${sourceObject.javaClass.name}.${fieldDefinition.name}"]?.call(sourceObject) ?: run {
                sourceObject::class.memberProperties
                    .find { it.name == fieldDefinition.name }
                    ?.let { kProperty ->
                        kProperty.getter.call(sourceObject).also {
                            register(sourceObject::class, kProperty)
                        }
                    }
            } ?: run {
                logger.error("getter method not found: ${sourceObject.javaClass.name}.${fieldDefinition.name}")
            }
        }

    override fun get(environment: DataFetchingEnvironment): Any? =
        environment.getSource<Any?>()?.let { sourceObject ->
            getters["${sourceObject.javaClass.name}.${environment.fieldDefinition.name}"]?.call(sourceObject)
        }
}
