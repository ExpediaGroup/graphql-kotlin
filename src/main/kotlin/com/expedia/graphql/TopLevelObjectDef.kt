package com.expedia.graphql

import kotlin.reflect.KClass

/**
 * Encapsulates an object to use as a target for schema generation and the
 * class to be used for reflection.
 *
 * @param obj   The target object (or proxy to target object)
 * @param klazz Optional class of the target (or the proxied object)
 */
data class TopLevelObjectDef(val obj: Any, val klazz: KClass<*> = obj::class)
