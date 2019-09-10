/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
