package com.expedia.graphql.schema.generator.types

import kotlin.reflect.KClass

internal fun getInputClassName(klass: KClass<*>): String? = klass.simpleName?.let { "${it}Input" }
