package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.customscalarquery.ScalarWrapper
import kotlin.String
import kotlin.reflect.KClass

const val CUSTOM_SCALAR_QUERY: String =
    "query CustomScalarQuery {\n  scalarQuery {\n    custom\n  }\n}"

class CustomScalarQuery : GraphQLClientRequest<CustomScalarQuery.Result> {
  override val query: String = CUSTOM_SCALAR_QUERY

  override val operationName: String = "CustomScalarQuery"

  override fun responseType(): KClass<CustomScalarQuery.Result> = CustomScalarQuery.Result::class

  data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    val scalarQuery: ScalarWrapper
  )
}
