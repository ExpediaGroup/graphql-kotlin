package com.expediagroup.graphql.client.jackson.data

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import kotlin.reflect.KClass

class EnumQuery : GraphQLClientRequest<EnumQuery.Result> {
    override val query: String = "ENUM_QUERY"

    override val operationName: String = "EnumQuery"

    override fun responseType(): KClass<Result> = Result::class

    enum class TestEnum {
        ONE,
        TWO,
        @JsonEnumDefaultValue
        __UNKNOWN
    }

    data class Result(
        val enumResult: TestEnum = TestEnum.__UNKNOWN
    )
}
