package com.expediagroup.graphql.client.serialization.types.data

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
class OtherQuery : GraphQLClientRequest<OtherQuery.Result> {
    override val query: String = "OTHER_QUERY"

    override val operationName: String = "OtherQuery"

    override fun responseType(): KClass<Result> = Result::class

    @Serializable
    data class Result(
        val stringResult: String,
        val integerResult: Int
    )
}
