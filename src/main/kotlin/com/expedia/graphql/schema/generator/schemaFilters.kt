package com.expedia.graphql.schema.generator

import com.expedia.graphql.schema.extensions.isGraphQLIgnored
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility

private val blackListFunctions: List<String> = listOf("annotationType", "toString", "copy", "equals", "hashCode")
private val componentFunctionRegex = Regex("component([0-9]+)")

private typealias CallableFilter = (KCallable<*>) -> Boolean
private typealias AnnotatedElementFilter = (KAnnotatedElement) -> Boolean

private val isPublic: CallableFilter = { it.visibility == KVisibility.PUBLIC }
private val isNotGraphQLIgnored: AnnotatedElementFilter = { it.isGraphQLIgnored().not() }

private typealias PropertyFilter = (KProperty<*>) -> Boolean
private typealias FunctionFilter = (KFunction<*>) -> Boolean

internal val isNotBlackListed: FunctionFilter = { (blackListFunctions.contains(it.name) || it.name.matches(componentFunctionRegex)).not() }

internal val propertyFilters: List<PropertyFilter> = listOf(isPublic, isNotGraphQLIgnored)
internal val functionFilters: List<FunctionFilter> = listOf(isPublic, isNotGraphQLIgnored, isNotBlackListed)
