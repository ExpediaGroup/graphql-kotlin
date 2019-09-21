/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.sample.validation

import com.expediagroup.graphql.execution.DataFetcherExecutionPredicate
import com.expediagroup.graphql.sample.exceptions.ValidationException
import com.expediagroup.graphql.sample.exceptions.asConstraintError
import graphql.schema.DataFetchingEnvironment
import javax.validation.Valid
import javax.validation.Validator
import kotlin.reflect.KParameter

class DataFetcherExecutionValidator(private val validator: Validator) : DataFetcherExecutionPredicate {

    override fun <T> evaluate(value: T, parameter: KParameter, environment: DataFetchingEnvironment): T {
        val parameterAnnotated = parameter.annotations.any { it.annotationClass == Valid::class }
        val validations = if (parameterAnnotated) {
            validator.validate(value)
        } else {
            emptySet()
        }

        if (validations.isEmpty()) {
            return value
        }
        else {
            throw ValidationException(validations.map { it.asConstraintError() })
        }
    }
}
