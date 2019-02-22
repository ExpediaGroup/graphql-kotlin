package com.expedia.graphql.sample.controllers

import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SDLController(
    val schema: GraphQLSchema,
    val schemaPrinter: SchemaPrinter
) {

    @GetMapping(
        produces = [MediaType.TEXT_PLAIN_VALUE],
        value = ["/sdl"]
    )
    fun sdl() = schemaPrinter.print(schema)
}
