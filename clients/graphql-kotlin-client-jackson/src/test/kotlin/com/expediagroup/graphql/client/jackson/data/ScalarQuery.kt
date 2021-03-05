package com.expediagroup.graphql.client.jackson.data

import com.expediagroup.graphql.client.jackson.data.scalars.UUID
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.reflect.KClass

// typealiases would be in separate file
typealias ID = String

class ScalarQuery : GraphQLClientRequest<ScalarQuery.Result> {
    override val query: String = "SCALAR_QUERY"

    override val operationName: String = "ScalarQuery"

    override fun responseType(): KClass<Result> = Result::class

    data class Result(
        val scalarAlias: ID,
        val customScalar: UUID
    )
}
