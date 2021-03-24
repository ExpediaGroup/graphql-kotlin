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

package com.expediagroup.graphql.client.jackson.data.polymorphicquery

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "__typename"
)
@JsonSubTypes(
    value = [
        com.fasterxml.jackson.annotation.JsonSubTypes.Type(
            value = FirstInterfaceImplementation::class,
            name = "FirstInterfaceImplementation"
        ), com.fasterxml.jackson.annotation.JsonSubTypes.Type(
            value = SecondInterfaceImplementation::class,
            name = "SecondInterfaceImplementation"
        )
    ]
)
interface BasicInterface {
    /**
     * Unique identifier of an interface
     */
    abstract val id: Int
}

/**
 * Example interface implementation where value is an integer
 */
data class FirstInterfaceImplementation(
    /**
     * Unique identifier of the first implementation
     */
    override val id: Int,
    /**
     * Custom field integer value
     */
    val intValue: Int
) : BasicInterface

/**
 * Example interface implementation where value is a float
 */
data class SecondInterfaceImplementation(
    /**
     * Unique identifier of the second implementation
     */
    override val id: Int,
    /**
     * Custom field float value
     */
    val floatValue: Float
) : BasicInterface
