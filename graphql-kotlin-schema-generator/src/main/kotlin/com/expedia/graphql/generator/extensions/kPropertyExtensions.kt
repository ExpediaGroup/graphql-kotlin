package com.expedia.graphql.generator.extensions

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * KProperty Extensions:
 * For properties we need to check both the property for annotations and the constructor argument
 */

internal fun KProperty<*>.isPropertyGraphQLID(parentClass: KClass<*>): Boolean = when {
    this.isGraphQLID() -> true
    getConstructorParameter(parentClass)?.isGraphQLID().isTrue() -> true
    else -> false
}

internal fun KProperty<*>.isPropertyGraphQLIgnored(parentClass: KClass<*>): Boolean = when {
    this.isGraphQLIgnored() -> true
    getConstructorParameter(parentClass)?.isGraphQLIgnored().isTrue() -> true
    else -> false
}

internal fun KProperty<*>.getPropertyDeprecationReason(parentClass: KClass<*>): String? =
    this.getDeprecationReason() ?: getConstructorParameter(parentClass)?.getDeprecationReason()

internal fun KProperty<*>.getPropertyDescription(parentClass: KClass<*>): String? =
    this.getGraphQLDescription() ?: getConstructorParameter(parentClass)?.getGraphQLDescription()

internal fun KProperty<*>.getPropertyName(parentClass: KClass<*>): String? =
    this.getGraphQLName() ?: getConstructorParameter(parentClass)?.getGraphQLName() ?: this.name

private fun KProperty<*>.getConstructorParameter(parentClass: KClass<*>) = parentClass.findConstructorParamter(this.name)
