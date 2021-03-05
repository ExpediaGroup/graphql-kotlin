package com.expediagroup.graphql.client.serialization.types.data

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
class EnumQuery : GraphQLClientRequest<EnumQuery.Result> {
    override val query: String = "ENUM_QUERY"

    override val operationName: String = "EnumQuery"

    override fun responseType(): KClass<Result> = Result::class

    enum class TestEnum {
        ONE,
        TWO,
        __UNKNOWN
    }

    @Serializable
    data class Result(
        val enumResult: TestEnum = TestEnum.__UNKNOWN
    )
}
