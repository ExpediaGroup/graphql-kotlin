package com.expediagroup.graphql.federation.types

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType

// TODO remove suppresion once we upgrade to latest Detekt version
/**
 * _Service object is returned by _service query and is used to encapsulate SDL.
 */
@Suppress("TopLevelPropertyNaming")
private val SERVICE_OBJECT_TYPE = GraphQLObjectType.newObject()
    .name("_Service")
    .field(
        GraphQLFieldDefinition.newFieldDefinition()
            .name("sdl")
            .type(GraphQLNonNull(Scalars.GraphQLString))
            .build())
    .build()

val SERVICE_FIELD_DEFINITION: GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
    .name("_service")
    .type(SERVICE_OBJECT_TYPE)
    .build()

@Suppress("ClassNaming")
data class _Service(val sdl: String)
