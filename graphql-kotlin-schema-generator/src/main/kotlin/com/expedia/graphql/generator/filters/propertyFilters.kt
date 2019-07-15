package com.expedia.graphql.generator.filters

import com.expedia.graphql.generator.extensions.isPropertyGraphQLIgnored
import com.expedia.graphql.generator.extensions.isPublic
import com.expedia.graphql.generator.extensions.qualifiedName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

private typealias PropertyFilter = (KProperty<*>, KClass<*>) -> Boolean

private val blacklistTypes: List<String> = listOf("kotlin.reflect.KClass")

private val isPropertyPublic: PropertyFilter = { prop, _ -> prop.isPublic() }
private val isPropertyNotGraphQLIgnored: PropertyFilter = { prop, parentClass -> prop.isPropertyGraphQLIgnored(parentClass).not() }
private val isNotBlacklistedType: PropertyFilter = { prop, _ -> blacklistTypes.contains(prop.returnType.qualifiedName).not() }

internal val propertyFilters: List<PropertyFilter> = listOf(isPropertyPublic, isNotBlacklistedType, isPropertyNotGraphQLIgnored)
