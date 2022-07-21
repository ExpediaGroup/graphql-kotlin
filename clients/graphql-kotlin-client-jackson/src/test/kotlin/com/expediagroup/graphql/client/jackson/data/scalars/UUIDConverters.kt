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

package com.expediagroup.graphql.client.jackson.data.scalars

import com.expediagroup.graphql.client.converter.ScalarConverter
import com.fasterxml.jackson.databind.util.StdConverter
import java.util.UUID
import kotlin.Any

class AnyToUUIDConverter : StdConverter<Any, UUID>() {
    private val converter: UUIDScalarConverter = UUIDScalarConverter()

    override fun convert(`value`: Any): UUID = converter.toScalar(value)
}

class UUIDToAnyConverter : StdConverter<UUID, Any>() {
    private val converter: UUIDScalarConverter = UUIDScalarConverter()

    override fun convert(`value`: UUID): Any = converter.toJson(value)
}

// scalar converter would not be part of the generated sources
class UUIDScalarConverter : ScalarConverter<UUID> {
    override fun toScalar(rawValue: Any): UUID = UUID.fromString(rawValue.toString())
    override fun toJson(value: UUID): String = value.toString()
}
