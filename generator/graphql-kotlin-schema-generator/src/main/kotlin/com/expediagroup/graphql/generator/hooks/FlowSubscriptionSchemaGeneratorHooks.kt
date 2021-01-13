package com.expediagroup.graphql.generator.hooks

import com.expediagroup.graphql.generator.internal.extensions.getTypeOfFirstArgument
import com.expediagroup.graphql.generator.internal.extensions.isSubclassOf
import kotlinx.coroutines.flow.Flow
import org.reactivestreams.Publisher
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType

/**
 * Subclassable [SchemaGeneratorHooks] implementation that supports
 * subscriptions that return either [Flow]s or [Publisher]s
 */
open class FlowSubscriptionSchemaGeneratorHooks : SchemaGeneratorHooks {
    /**
     * Unwrap a [Flow] to its argument type
     */
    override fun willResolveMonad(type: KType): KType {
        return when {
            type.isSubclassOf(Flow::class) -> type.getTypeOfFirstArgument()
            else -> super.willResolveMonad(type)
        }
    }

    /**
     * Allow for [Flow] subscription types
     */
    override fun isValidSubscriptionReturnType(kClass: KClass<*>, function: KFunction<*>): Boolean {
        return function.returnType.isSubclassOf(Flow::class) || super.isValidSubscriptionReturnType(kClass, function)
    }
}
