package com.expediagroup.graphql.client.jackson.data

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class FirstQuery(
    override val variables: Variables
) : GraphQLClientRequest<FirstQuery.Result> {
    override val query: String = "FIRST_QUERY"

    override val operationName: String = "FirstQuery"

    override fun responseType(): KClass<Result> = Result::class

    data class Variables(
        val input: Float? = null
    )

    data class Result(
        val stringResult: String
    )
}
