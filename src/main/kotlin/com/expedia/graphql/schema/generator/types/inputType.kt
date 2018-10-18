package com.expedia.graphql.schema.generator.types

import kotlin.reflect.KClass

internal fun getGraphQLInputClassName(klass: KClass<*>): String? {
    val simpleName = klass.simpleName
    return if (simpleName != null) "${simpleName}Input" else simpleName
}
