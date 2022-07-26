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
import kotlin.Any

class AnyToAnyConverter : StdConverter<Any, Any>() {
    private val converter: AnyScalarConverter = AnyScalarConverter()

    override fun convert(`value`: Any): Any = converter.toScalar(value)
}

// scalar converter would not be part of the generated sources
class AnyScalarConverter : ScalarConverter<Any> {
    override fun toScalar(rawValue: Any): Any = rawValue
    override fun toJson(value: Any): Any = value
}

// representation would not be part of the generated sources
data class ProductEntityRepresentation(val id: String) {
    val __typename: String = "Product"
}
