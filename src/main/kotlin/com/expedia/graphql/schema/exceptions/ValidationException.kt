package com.expedia.graphql.schema.exceptions

import javax.validation.ConstraintViolation

class ValidationException(val errors: List<ValidationError>): RuntimeException("The query or mutation due to a validation error")


class ValidationError(constraintViolation: ConstraintViolation<*>) {
    val path: String = constraintViolation.propertyPath.toString()
    val message: String = constraintViolation.message
    val type: String = constraintViolation.leafBean.toString()
}
