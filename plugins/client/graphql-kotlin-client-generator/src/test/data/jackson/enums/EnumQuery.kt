package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.enums.CustomEnum
import kotlin.String
import kotlin.reflect.KClass

const val ENUM_QUERY: String = "query EnumQuery {\n  enumQuery\n}"

class EnumQuery : GraphQLClientRequest<EnumQuery.Result> {
  override val query: String = ENUM_QUERY

  override val operationName: String = "EnumQuery"

  override fun responseType(): KClass<EnumQuery.Result> = EnumQuery.Result::class

  data class Result(
    /**
     * Query that returns enum value
     */
    val enumQuery: CustomEnum = CustomEnum.__UNKNOWN_VALUE
  )
}
