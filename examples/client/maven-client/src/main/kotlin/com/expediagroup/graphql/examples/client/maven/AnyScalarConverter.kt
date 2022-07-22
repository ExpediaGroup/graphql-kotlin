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

package com.expediagroup.graphql.examples.client.maven

import com.expediagroup.graphql.client.converter.ScalarConverter

/**
 * Simple pass-through converter to allow specifying arbitrary objects for custom scalars.
 */
class AnyScalarConverter : ScalarConverter<Any> {
    override fun toScalar(rawValue: Any): Any = rawValue
    override fun toJson(value: Any): Any = value
}
