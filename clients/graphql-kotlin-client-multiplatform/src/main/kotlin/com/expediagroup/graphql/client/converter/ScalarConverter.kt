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

package com.expediagroup.graphql.client.converter

/**
 * Custom GraphQL scalar JSON converter that is used to convert between raw JSON String representation and typesafe scalar value.
 */
interface ScalarConverter<T> {

    /**
     * Deserialize raw JSON value to a typesafe value.
     */
    fun toScalar(rawValue: Any): T

    /**
     * Serialize typesafe scalar value to a raw JSON value.
     */
    fun toJson(value: T): Any
}
