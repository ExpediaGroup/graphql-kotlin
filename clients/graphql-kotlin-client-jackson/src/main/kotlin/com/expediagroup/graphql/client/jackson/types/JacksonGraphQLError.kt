package com.expediagroup.graphql.client.jackson.types

import com.expediagroup.graphql.client.types.GraphQLError
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class JacksonGraphQLError(
    override val message: String,
    override val locations: List<JacksonSourceLocation>? = null,
    override val path: List<Any>? = null,
    override val extensions: Map<String, Any?>? = null
) : GraphQLError
