package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.CouldNotCastArgumentException
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

@Throws(CouldNotCastArgumentException::class)
internal fun KParameter.javaTypeClass(): Class<*> = this.type.javaType as? Class<*> ?: throw CouldNotCastArgumentException(this)
