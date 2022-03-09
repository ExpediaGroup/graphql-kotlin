package com.expediagroup.graphql.generator.scalars

import graphql.execution.ValueUnboxer

/**
 * ID is a value class which may be represented at runtime as wrapper or directly as underlying type.
 *
 * We need to explicitly unwrap it as due to the generic nature of the query processing logic we always end up
 * with up a wrapper type when resolving the field value.
 */
open class IDValueUnboxer : ValueUnboxer {
    override fun unbox(`object`: Any?): Any? = if (`object` is ID) {
        `object`.value
    } else {
        `object`
    }
}
