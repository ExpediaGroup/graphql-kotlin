package com.expediagroup.graphql.client.serialization.types

import com.expediagroup.graphql.client.types.SourceLocation
import kotlinx.serialization.Serializable

@Serializable
class KotlinXSourceLocation(
    override val line: Int,
    override val column: Int
) : SourceLocation
