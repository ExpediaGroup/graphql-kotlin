package com.expedia.graphql

import com.expedia.graphql.annotations.GraphQLContext
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlin.reflect.KFunction

private val mapper = jacksonObjectMapper()

/**
 * Custom wrapper for a class and its annotations
 */
internal data class Parameter(val klazz: Class<*>, val annotations: List<Annotation>)

/**
 * Interface that exposes access to the [instrumentable] property for
 * internal [KotlinDataFetcher]
 */
interface Instrumentable {
    /**
     * Determines whether or not the fetcher should be instrumented
     * using GraphQL instrumentation
     */
    val instrumentable: Boolean
}

/**
 * Simple DataFetcher that invokes function on the target object.
 *
 * @param target    The target object that performs the data fetching
 * @param fn        The Kotlin function being invoked
 * @param args      The GraphQL arguments passed to the data fetcher
 */
internal class KotlinDataFetcher(
        val target: Any?,
        val fn: KFunction<*>,
        val args: Map<String, Parameter>,
        override val instrumentable: Boolean
) : DataFetcher<Any>, Instrumentable {

    override fun get(environment: DataFetchingEnvironment): Any? {
        val instance = target ?: environment.getSource<Any>()
        return if (null != instance) {
            @Suppress("Detekt.SpreadOperator")
            fn.call(instance, *args.map {
                val name = it.key
                val klazz = it.value.klazz
                val annotations = it.value.annotations

                if (annotations.any { it.annotationClass == GraphQLContext::class }) {
                    environment.getContext()
                } else {
                    mapper.convertValue(environment.arguments[name], klazz)
                }
            }.toTypedArray())
        } else {
            null
        }
    }
}
