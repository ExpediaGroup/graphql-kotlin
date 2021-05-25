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

package com.expediagroup.graphql.client.serialization.types

import com.expediagroup.graphql.client.serialization.serializers.OptionalInputSerializer
import kotlinx.serialization.Serializable

@Serializable(with = OptionalInputSerializer::class)
sealed class OptionalInput<out T> {
    /**
     * Represents missing/undefined value.
     */
    @Serializable(with = OptionalInputSerializer::class)
    object Undefined : OptionalInput<Nothing>() {
        override fun toString() = "UNDEFINED"
    }

    /**
     * Wrapper holding explicitly specified value including NULL.
     */
    @Serializable(with = OptionalInputSerializer::class)
    data class Defined<out U>(val value: U?) : OptionalInput<U>() {
        override fun toString(): String = "Defined(value=$value)"
    }
}
