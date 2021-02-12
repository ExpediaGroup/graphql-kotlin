package com.expediagroup.graphql.client.jackson.types

import com.expediagroup.graphql.client.types.SourceLocation
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class JacksonSourceLocation(
    override val line: Int,
    override val column: Int
) : SourceLocation
