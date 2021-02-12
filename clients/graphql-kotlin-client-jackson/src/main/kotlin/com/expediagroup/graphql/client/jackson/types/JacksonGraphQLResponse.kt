package com.expediagroup.graphql.client.jackson.types

import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class JacksonGraphQLResponse<T>(
    override val data: T? = null,
    override val errors: List<JacksonGraphQLError>? = null,
    override val extensions: Map<String, Any>? = null
) : GraphQLClientResponse<T>
