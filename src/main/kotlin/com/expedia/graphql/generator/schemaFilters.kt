package com.expedia.graphql.generator

import com.expedia.graphql.generator.extensions.isGraphQLIgnored
import com.expedia.graphql.generator.extensions.isPropertyGraphQLIgnored
import com.expedia.graphql.generator.extensions.qualifiedName
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility

private typealias CallableFilter = (KCallable<*>) -> Boolean
private typealias PropertyFilter = (KProperty<*>, KClass<*>) -> Boolean
private typealias FunctionFilter = (KFunction<*>) -> Boolean

private val blacklistFunctions: List<String> = listOf("annotationType", "toString", "copy", "equals", "hashCode")
private val blacklistTypes: List<String> = listOf("kotlin.reflect.KClass")
private val componentFunctionRegex = Regex("component([0-9]+)")

private val isPublic: CallableFilter = { it.visibility == KVisibility.PUBLIC }
private val isPropertyPublic: PropertyFilter = { prop, _ -> isPublic(prop) }
private val isNotGraphQLIgnored: CallableFilter = { it.isGraphQLIgnored().not() }
private val isPropertyNotGraphQLIgnored: PropertyFilter = { prop, parentClass -> prop.isPropertyGraphQLIgnored(parentClass).not() }
private val isNotBlacklistedType: PropertyFilter = { prop, _ -> blacklistTypes.contains(prop.returnType.qualifiedName).not() }
private val isBlacklistedFunction: CallableFilter = { blacklistFunctions.contains(it.name) }
private val isComponentFunction: CallableFilter = { it.name.matches(componentFunctionRegex) }
private val isNotBlacklistedFunction: CallableFilter = { isBlacklistedFunction(it).not() && isComponentFunction(it).not() }

internal val propertyFilters: List<PropertyFilter> = listOf(isPropertyPublic, isNotBlacklistedType, isPropertyNotGraphQLIgnored)
internal val functionFilters: List<FunctionFilter> = listOf(isPublic, isNotGraphQLIgnored, isNotBlacklistedFunction)
