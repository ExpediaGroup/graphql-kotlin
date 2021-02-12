package com.expediagroup.graphql.client.serialization.types

import com.expediagroup.graphql.client.types.GraphQLError
import kotlinx.serialization.Serializable

@Serializable
class KotlinXGraphQLError(
    override val message: String,
    override val locations: List<KotlinXSourceLocation>? = null,
    override val path: List<@Serializable(with = AnyKSerializer::class) Any>? = null,
    override val extensions: Map<String, @Serializable(with = AnyKSerializer::class) Any?>? = null
) : GraphQLError
