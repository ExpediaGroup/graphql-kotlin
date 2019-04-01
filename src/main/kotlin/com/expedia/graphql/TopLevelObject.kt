package com.expedia.graphql

import kotlin.reflect.KClass

/**
 * Encapsulates an object to use as a target for schema generation and the
 * class to be used for reflection.
 *
 * @param obj The target object (or proxy to target object)
 * @param kClass Optional KClass of the target (or the proxied object)
 */
data class TopLevelObject(val obj: Any?, val kClass: KClass<*>) {
    constructor(obj: Any) : this(obj, obj::class)
    constructor(kClass: KClass<*>) : this(null, kClass)
}
