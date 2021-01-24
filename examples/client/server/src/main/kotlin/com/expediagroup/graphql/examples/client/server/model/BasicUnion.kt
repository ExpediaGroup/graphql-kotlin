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

@GraphQLDescription("Very basic union of BasicObject and ComplexObject")
interface BasicUnion

@GraphQLDescription("Some basic description")
data class BasicObject(val id: Int, @GraphQLDescription("Object name") val name: String) : BasicUnion

@GraphQLDescription(
    """Multi line description of a complex type.
        This is a second line of the paragraph.
        This is final line of the description."""
)
data class ComplexObject(
    @GraphQLDescription("Some unique identifier")
    val id: Int,
    @GraphQLDescription("Some object name")
    val name: String,
    @GraphQLDescription(
        """Optional value
            |Second line of the description"""
    )
    val optional: String? = null,
    @GraphQLDescription("Some additional details")
    val details: DetailsObject
) : BasicUnion

@GraphQLDescription("Inner type object description")
data class DetailsObject(
    @GraphQLDescription("Unique identifier")
    val id: Int,
    @GraphQLDescription("Boolean flag")
    val flag: Boolean,
    @GraphQLDescription("Actual detail value")
    val value: String
)
