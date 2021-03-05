package com.expediagroup.graphql.client.jackson.data

import com.expediagroup.graphql.client.jackson.data.polymorphicquery.BasicInterface
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

class PolymorphicQuery : GraphQLClientRequest<PolymorphicQuery.Result> {
    override val query: String = "POLYMORPHIC_QUERY"

    override val operationName: String = "PolymorphicQuery"

    override fun responseType(): KClass<Result> = Result::class

    data class Result(
        val polymorphicResult: BasicInterface
    )
}
