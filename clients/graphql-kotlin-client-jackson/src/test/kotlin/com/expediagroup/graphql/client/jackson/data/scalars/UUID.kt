/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.client.jackson.data.scalars

import com.expediagroup.graphql.client.converter.ScalarConverter
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class UUID(
    val value: java.util.UUID
) {
    @JsonValue
    fun rawValue() = converter.toJson(value)

    companion object {
        val converter: UUIDScalarConverter = UUIDScalarConverter()

        @JsonCreator
        @JvmStatic
        fun create(rawValue: Any) = UUID(converter.toScalar(rawValue))
    }
}

// scalar converter would not be part of the generated sources
class UUIDScalarConverter : ScalarConverter<java.util.UUID> {
    override fun toScalar(rawValue: Any): java.util.UUID = java.util.UUID.fromString(rawValue.toString())
    override fun toJson(value: java.util.UUID): String = value.toString()
}
