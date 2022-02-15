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

package com.expediagroup.graphql.client.serialization.data

import com.expediagroup.graphql.client.serialization.data.scalars.OptionalInputSerializer
import com.expediagroup.graphql.client.serialization.data.scalars.UUIDSerializer
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.reflect.KClass

@Serializable
class OptionalInputQuery(
    override val variables: Variables
) : GraphQLClientRequest<OptionalInputQuery.Result> {
    @Required
    override val query: String = "OPTIONAL_INPUT_QUERY"

    @Required
    override val operationName: String = "OptionalInputQuery"

    override fun responseType(): KClass<Result> = Result::class

    @Serializable
    data class Variables(
        val requiredInput: Int,
        @Serializable(with = OptionalInputSerializer::class)
        val optionalIntInput: OptionalInput<Int> = OptionalInput.Undefined,
        @Serializable(with = OptionalInputSerializer::class)
        val optionalStringInput: OptionalInput<String> = OptionalInput.Undefined,
        @Serializable(with = OptionalInputSerializer::class)
        val optionalBooleanInput: OptionalInput<Boolean> = OptionalInput.Undefined,
        @Serializable(with = OptionalInputSerializer::class)
        val optionalUUIDInput: OptionalInput<@Serializable(with = UUIDSerializer::class) UUID> = OptionalInput.Undefined,
        @Serializable(with = OptionalInputSerializer::class)
        val optionalUUIDListInput: OptionalInput<List<@Serializable(with = UUIDSerializer::class) UUID>> = OptionalInput.Undefined,
    )

    @Serializable
    data class Result(
        val stringResult: String
    )
}
