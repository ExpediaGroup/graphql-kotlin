package com.expedia.graphql.sample.exceptions

import java.lang.RuntimeException
import javax.validation.ConstraintViolation


class ValidationException(val constraintErrors: List<ConstraintError>) : RuntimeException("Validation error")

data class ConstraintError(private val constraintViolation: ConstraintViolation<*>,
                           val path: String = constraintViolation.propertyPath.toString(),
                           val message: String = constraintViolation.message,
                           val type: String = constraintViolation.leafBean.toString()
)