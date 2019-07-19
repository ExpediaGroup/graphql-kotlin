package com.expedia.graphql.generator.extensions

import kotlin.reflect.KCallable
import kotlin.reflect.KVisibility

internal fun KCallable<*>.isPublic(): Boolean = this.visibility == KVisibility.PUBLIC

internal fun KCallable<*>.getFunctionName(): String = this.getGraphQLName() ?: this.name
