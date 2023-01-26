/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern

@Validated
@Component
class ValidatedQuery : Query {
    fun argumentWithValidation(@Valid arg: TypeWithPattern): String = arg.lowerCaseOnly
}

data class TypeWithPattern(
    @field:Pattern(regexp = "^[a-z]+$", message = "Argument must be lowercase")
    val lowerCaseOnly: String
)
