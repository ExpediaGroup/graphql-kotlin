package com.expedia.graphql.generator.filters

import com.expedia.graphql.generator.extensions.isInterface
import com.expedia.graphql.generator.extensions.isPublic
import com.expedia.graphql.generator.extensions.isUnion
import kotlin.reflect.KClass

private typealias SuperclassFilter = (KClass<*>) -> Boolean

private val isPublic: SuperclassFilter = { it.isPublic() }
private val isInterface: SuperclassFilter = { it.isInterface() || it.isAbstract }
private val isNotUnion: SuperclassFilter = { it.isUnion().not() }

internal val superclassFilters: List<SuperclassFilter> = listOf(isPublic, isInterface, isNotUnion)
