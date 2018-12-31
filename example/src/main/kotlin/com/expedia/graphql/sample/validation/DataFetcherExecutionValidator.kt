package com.expedia.graphql.sample.validation

import com.expedia.graphql.hooks.DataFetcherExecutionPredicate
import com.expedia.graphql.sample.exceptions.ValidationException
import com.expedia.graphql.sample.exceptions.asConstraintError
import graphql.schema.DataFetchingEnvironment
import javax.validation.ConstraintViolation
import javax.validation.Valid
import javax.validation.Validator
import kotlin.reflect.KParameter

class DataFetcherExecutionValidator(private val validator: Validator) : DataFetcherExecutionPredicate() {

    override fun <T> evaluate(value: T, parameter: KParameter, environment: DataFetchingEnvironment): Any {
        val parameterAnnotated = parameter.annotations.any { it.annotationClass == Valid::class }
        return if (parameterAnnotated) {
            validator.validate(value)
        } else {
            emptySet()
        }
    }

    @Throws(ValidationException::class)
    override fun onFailure(evaluationResult: Any, parameter: KParameter, environment: DataFetchingEnvironment): Nothing {
        val violations = evaluationResult as Set<ConstraintViolation<*>>
        throw ValidationException(violations.map { it.asConstraintError() })
    }

    override fun test(evaluationResult: Any): Boolean = evaluationResult is Set<*> && evaluationResult.isEmpty()
}
