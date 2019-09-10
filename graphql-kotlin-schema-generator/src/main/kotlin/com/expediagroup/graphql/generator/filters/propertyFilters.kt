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

import com.expediagroup.graphql.generator.extensions.isPropertyGraphQLIgnored
import com.expediagroup.graphql.generator.extensions.isPublic
import com.expediagroup.graphql.generator.extensions.qualifiedName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

private typealias PropertyFilter = (KProperty<*>, KClass<*>) -> Boolean

private val blacklistTypes: List<String> = listOf("kotlin.reflect.KClass")

private val isPropertyPublic: PropertyFilter = { prop, _ -> prop.isPublic() }
private val isPropertyNotGraphQLIgnored: PropertyFilter = { prop, parentClass -> prop.isPropertyGraphQLIgnored(parentClass).not() }
private val isNotBlacklistedType: PropertyFilter = { prop, _ -> blacklistTypes.contains(prop.returnType.qualifiedName).not() }

internal val propertyFilters: List<PropertyFilter> = listOf(isPropertyPublic, isNotBlacklistedType, isPropertyNotGraphQLIgnored)
