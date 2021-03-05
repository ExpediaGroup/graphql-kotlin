package com.expediagroup.graphql.client.jackson.data

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class OtherQuery : GraphQLClientRequest<OtherQuery.Result> {
    override val query: String = "OTHER_QUERY"

    override val operationName: String = "OtherQuery"

    override fun responseType(): KClass<Result> = Result::class

    data class Result(
        val stringResult: String,
        val integerResult: Int
    )
}
