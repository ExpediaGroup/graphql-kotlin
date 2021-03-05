package com.expediagroup.graphql.client.serialization.types.data

import com.expediagroup.graphql.client.serialization.types.data.polymorphicquery.BasicInterface
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
class PolymorphicQuery : GraphQLClientRequest<PolymorphicQuery.Result> {
    override val query: String = "POLYMORPHIC_QUERY"

    override val operationName: String = "PolymorphicQuery"

    override fun responseType(): KClass<Result> = Result::class

    @Serializable
    data class Result(
        val polymorphicResult: BasicInterface
    )
}
