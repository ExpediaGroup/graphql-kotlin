package com.expediagroup.graphql.client.serialization.types.data

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
class FirstQuery(
    override val variables: Variables
) : GraphQLClientRequest<FirstQuery.Result> {
    override val query: String = "FIRST_QUERY"

    override val operationName: String = "FirstQuery"

    override fun responseType(): KClass<Result> = Result::class

    @Serializable
    data class Variables(
        val input: Float? = null
    )

    @Serializable
    data class Result(
        val stringResult: String
    )
}
