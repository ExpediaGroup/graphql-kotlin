/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.filters

import com.expediagroup.graphql.generator.internal.extensions.isGraphQLIgnored
import com.expediagroup.graphql.generator.internal.extensions.isInterface
import com.expediagroup.graphql.generator.internal.extensions.isPublic
import com.expediagroup.graphql.generator.internal.extensions.isUnion
import kotlin.reflect.KClass

private typealias SuperclassFilter = (KClass<*>) -> Boolean

private val isPublic: SuperclassFilter = { it.isPublic() }
private val isInterface: SuperclassFilter = { it.isInterface() }
private val isNotUnion: SuperclassFilter = { it.isUnion().not() }
private val isNotIgnored: SuperclassFilter = { it.isGraphQLIgnored().not() }

internal val superclassFilters: List<SuperclassFilter> = listOf(isPublic, isNotIgnored, isInterface, isNotUnion)
