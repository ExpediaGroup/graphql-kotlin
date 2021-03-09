package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.customscalarquery.ScalarWrapper
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val CUSTOM_SCALAR_QUERY: String =
    "query CustomScalarQuery {\n  first: scalarQuery {\n    ... scalarSelections\n  }\n  second: scalarQuery {\n    ... scalarSelections\n  }\n}\nfragment scalarSelections on ScalarWrapper {\n  count\n  custom\n  id\n}"

@Serializable
class CustomScalarQuery : GraphQLClientRequest<CustomScalarQuery.Result> {
  override val query: String = CUSTOM_SCALAR_QUERY

  override val operationName: String = "CustomScalarQuery"

  override fun responseType(): KClass<CustomScalarQuery.Result> = CustomScalarQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    val first: ScalarWrapper,
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    val second: ScalarWrapper
  )
}
