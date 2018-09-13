@file:Suppress("Detekt.UndocumentedPublicClass")
package com.expedia.graphql

import com.expedia.graphql.annotations.GraphQLContext
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlin.reflect.KFunction

private val mapper = jacksonObjectMapper()

data class Parameter(val klazz: Class<*>, val annotations: List<Annotation>)

/**
 * Simple DataFetcher that invokes function on the target object.
 */
class KotlinDataFetcher(val target: Any?, val fn: KFunction<*>, val args: Map<String, Parameter>, val instrumentable: Boolean) : DataFetcher<Any> {

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
