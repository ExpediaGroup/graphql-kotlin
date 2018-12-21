package com.expedia.graphql.generator.state

import kotlin.reflect.KType

internal data class TypesCacheKey(val type: KType, val inputType: Boolean)
