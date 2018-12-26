package com.expedia.graphql.generator

import com.expedia.graphql.generator.extensions.isGraphQLIgnored
import com.expedia.graphql.generator.extensions.isPropertyGraphQLIgnored
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility

private typealias CallableFilter = (KCallable<*>) -> Boolean
private typealias AnnotatedElementFilter = (KAnnotatedElement) -> Boolean
private typealias PropertyFilter = (KProperty<*>, KClass<*>) -> Boolean
private typealias FunctionFilter = (KFunction<*>) -> Boolean

private val blackListFunctions: List<String> = listOf("annotationType", "toString", "copy", "equals", "hashCode")
private val componentFunctionRegex = Regex("component([0-9]+)")

private val isPublic: CallableFilter = { it.visibility == KVisibility.PUBLIC }
private val isPropertyPublic: PropertyFilter = { prop, _ -> isPublic(prop) }
private val isNotGraphQLIgnored: AnnotatedElementFilter = { it.isGraphQLIgnored().not() }
private val isPropertyNotGraphQLIgnored: PropertyFilter = { prop, parentClass -> prop.isPropertyGraphQLIgnored(parentClass).not() }
private val isBlacklistedFunction: FunctionFilter = { blackListFunctions.contains(it.name) }
private val isComponentFunction: FunctionFilter = { it.name.matches(componentFunctionRegex) }
private val isNotBlackListed: FunctionFilter = { (isBlacklistedFunction(it) || isComponentFunction(it)).not() }

internal val propertyFilters: List<PropertyFilter> = listOf(isPropertyPublic, isPropertyNotGraphQLIgnored)
internal val functionFilters: List<FunctionFilter> = listOf(isPublic, isNotGraphQLIgnored, isNotBlackListed)
