package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.scalaraliasquery.ScalarWrapper
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val SCALAR_ALIAS_QUERY: String =
    "query ScalarAliasQuery {\n  scalarQuery {\n    id\n    custom\n  }\n}"

@Serializable
class ScalarAliasQuery : GraphQLClientRequest<ScalarAliasQuery.Result> {
  override val query: String = SCALAR_ALIAS_QUERY

  override val operationName: String = "ScalarAliasQuery"

  override fun responseType(): KClass<ScalarAliasQuery.Result> = ScalarAliasQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    val scalarQuery: ScalarWrapper
  )
}
