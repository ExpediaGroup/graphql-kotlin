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

package com.expediagroup.graphql.generator.federation.types

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType

/**
 * _Service object is returned by _service query and is used to encapsulate SDL.
 */
private val SERVICE_OBJECT_TYPE = GraphQLObjectType.newObject()
    .name("_Service")
    .field(
        GraphQLFieldDefinition.newFieldDefinition()
            .name("sdl")
            .type(GraphQLNonNull(Scalars.GraphQLString))
            .build()
    )
    .build()

internal val SERVICE_FIELD_DEFINITION: GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
    .name("_service")
    .type(GraphQLNonNull(SERVICE_OBJECT_TYPE))
    .build()

@Suppress("ClassNaming", "ClassName")
internal data class _Service(val sdl: String)
