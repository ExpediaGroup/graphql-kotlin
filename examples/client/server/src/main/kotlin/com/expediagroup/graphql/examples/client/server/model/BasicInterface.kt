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

package com.expediagroup.graphql.examples.client.server.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Very basic interface")
interface BasicInterface {
    @GraphQLDescription("Unique identifier of an interface")
    val id: Int
    @GraphQLDescription("Name field")
    val name: String
}

@GraphQLDescription("Example interface implementation where value is an integer")
data class FirstInterfaceImplementation(
    @GraphQLDescription("Unique identifier of the first implementation")
    override val id: Int,
    @GraphQLDescription("Name of the first implementation")
    override val name: String,
    @GraphQLDescription("Custom field integer value")
    val intValue: Int
) : BasicInterface

@GraphQLDescription("Example interface implementation where value is a float")
data class SecondInterfaceImplementation(
    @GraphQLDescription("Unique identifier of the second implementation")
    override val id: Int,
    @GraphQLDescription("Name of the second implementation")
    override val name: String,
    @GraphQLDescription("Custom field float value")
    val floatValue: Float
) : BasicInterface
