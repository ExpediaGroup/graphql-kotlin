package com.expedia.graphql.sample.exceptions

import javax.validation.ConstraintViolation

class ValidationException(val constraintErrors: List<ConstraintError>) : RuntimeException("Validation error")

data class ConstraintError(val path: String, val message: String, val type: String)

fun ConstraintViolation<*>.asConstraintError() = ConstraintError(
    path = this.propertyPath.toString(),
    message = this.message,
    type = this.leafBean.toString()
)