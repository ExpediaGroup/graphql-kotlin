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

package com.expediagroup.graphql.client.serialization.data.polymorphicquery

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BasicInterface {
    /**
     * Unique identifier of an interface
     */
    abstract val id: Int
}

/**
 * Example interface implementation where value is an integer
 */
@Serializable
@SerialName(value = "FirstInterfaceImplementation")
data class FirstInterfaceImplementation(
    /**
     * Unique identifier of the first implementation
     */
    override val id: Int,
    /**
     * Custom field integer value
     */
    val intValue: Int
) : BasicInterface()

/**
 * Example interface implementation where value is a float
 */
@Serializable
@SerialName(value = "SecondInterfaceImplementation")
data class SecondInterfaceImplementation(
    /**
     * Unique identifier of the second implementation
     */
    override val id: Int,
    /**
     * Custom field float value
     */
    val floatValue: Float
) : BasicInterface()
