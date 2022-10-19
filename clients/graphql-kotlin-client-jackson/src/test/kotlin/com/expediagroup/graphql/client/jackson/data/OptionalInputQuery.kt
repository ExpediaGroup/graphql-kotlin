/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.client.jackson.data

import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.reflect.KClass

class OptionalInputQuery(
    override val variables: Variables
) : GraphQLClientRequest<OptionalInputQuery.Result> {
    override val query: String = "OPTIONAL_INPUT_QUERY"

    override val operationName: String = "OptionalInputQuery"

    override fun responseType(): KClass<Result> = Result::class

    data class Variables(
        @get:JsonProperty("requiredInput")
        val requiredInput: Int,
        @get:JsonProperty("optionalIntInput")
        val optionalIntInput: OptionalInput<Int> = OptionalInput.Undefined,
        @get:JsonProperty("optionalStringInput")
        val optionalStringInput: OptionalInput<String> = OptionalInput.Undefined,
        @get:JsonProperty("optionalBooleanInput")
        val optionalBooleanInput: OptionalInput<Boolean> = OptionalInput.Undefined
    )

    data class Result(
        val stringResult: String
    )
}
