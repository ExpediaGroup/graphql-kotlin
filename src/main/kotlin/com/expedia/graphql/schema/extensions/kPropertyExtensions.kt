package com.expedia.graphql.schema.extensions

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal fun KProperty<*>.isPropertyGraphQLID(parentClass: KClass<*>): Boolean = when {
    this.isGraphQLID() -> true
    parentClass.findConstructorParamter(this.name)
        ?.isGraphQLID()
        .isTrue() -> true
    else -> false
}

internal fun KProperty<*>.isPropertyGraphQLIgnored(parentClass: KClass<*>): Boolean = when {
    this.isGraphQLIgnored() -> true
    parentClass.findConstructorParamter(this.name)
        ?.isGraphQLIgnored()
        .isTrue() -> true
    else -> false
}

internal fun KProperty<*>.getPropertyDeprecationReason(parentClass: KClass<*>): String? =
    this.getDeprecationReason() ?: parentClass.findConstructorParamter(this.name)?.getDeprecationReason()

internal fun KProperty<*>.getPropertyDescription(parentClass: KClass<*>): String? =
    this.getGraphQLDescription() ?: parentClass.findConstructorParamter(this.name)?.getGraphQLDescription()
