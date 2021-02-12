package com.expediagroup.graphql.client.serialization.types

import com.expediagroup.graphql.client.types.GraphQLClientResponse
import kotlinx.serialization.Serializable

@Serializable
class KotlinXGraphQLResponse<T>(
    override val data: T? = null,
    override val errors: List<KotlinXGraphQLError>? = null,
    override val extensions: Map<String, @Serializable(with = AnyKSerializer::class) Any>? = null
) : GraphQLClientResponse<T>
