package com.expedia.graphql.generator.extensions

import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

@Suppress("Detekt.UnsafeCast")
internal val KType.javaTypeClass: Class<*>
    get() = this.javaType as Class<*>
