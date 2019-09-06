package com.expediagroup.graphql.generator.filters

import com.expediagroup.graphql.generator.extensions.isGraphQLIgnored
import com.expediagroup.graphql.generator.extensions.isPublic
import kotlin.reflect.KCallable

private typealias CallableFilter = (KCallable<*>) -> Boolean

private val blacklistFunctions: List<String> = listOf("annotationType", "toString", "copy", "equals", "hashCode")
private val componentFunctionRegex = Regex("component([0-9]+)")

private val isPublic: CallableFilter = { it.isPublic() }
private val isNotGraphQLIgnored: CallableFilter = { it.isGraphQLIgnored().not() }
private val isBlacklistedFunction: CallableFilter = { blacklistFunctions.contains(it.name) }
private val isComponentFunction: CallableFilter = { it.name.matches(componentFunctionRegex) }
private val isNotBlacklistedFunction: CallableFilter = { isBlacklistedFunction(it).not() && isComponentFunction(it).not() }

internal val functionFilters: List<CallableFilter> = listOf(isPublic, isNotGraphQLIgnored, isNotBlacklistedFunction)
