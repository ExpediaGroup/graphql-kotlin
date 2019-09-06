package com.expediagroup.graphql.generator.filters

import com.expediagroup.graphql.generator.extensions.isInterface
import com.expediagroup.graphql.generator.extensions.isPublic
import com.expediagroup.graphql.generator.extensions.isUnion
import kotlin.reflect.KClass

private typealias SuperclassFilter = (KClass<*>) -> Boolean

private val isPublic: SuperclassFilter = { it.isPublic() }
private val isInterface: SuperclassFilter = { it.isInterface() }
private val isNotUnion: SuperclassFilter = { it.isUnion().not() }

internal val superclassFilters: List<SuperclassFilter> = listOf(isPublic, isInterface, isNotUnion)
