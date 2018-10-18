package com.expedia.graphql.sample.validation

import com.expedia.graphql.sample.exceptions.ConstraintError
import com.expedia.graphql.sample.exceptions.ValidationException
import com.expedia.graphql.schema.Parameter
import com.expedia.graphql.schema.hooks.DataFetcherExecutionPredicate
import graphql.schema.DataFetchingEnvironment
import javax.validation.ConstraintViolation
import javax.validation.Valid
import javax.validation.Validator

class DataFetcherExecutionValidator(private val validator: Validator?) : DataFetcherExecutionPredicate() {

    override fun <T> evaluate(value: T, parameter: Parameter, argumentName: String, environment: DataFetchingEnvironment): Any {
        val parameterAnnotated = parameter.annotations.any { it.annotationClass == Valid::class }
        return if (validator != null && parameterAnnotated) {
            validator.validate(value)
        } else {
            emptySet()
        }
    }

    override fun onFailure(result: Any, parameter: Parameter, argumentName: String, environment: DataFetchingEnvironment): Nothing {
        val violations = result as Set<ConstraintViolation<*>>
        throw ValidationException(violations.map { ConstraintError(it) })
    }

    override fun test(result: Any): Boolean = result is Set<*> && result.isEmpty()
}
