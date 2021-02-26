package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val ALIAS_QUERY: String =
    "query AliasQuery {\n  first: inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n  second: inputObjectQuery(criteria: { min: 5.0, max: 10.0 } )\n}"

@Serializable
class AliasQuery : GraphQLClientRequest<AliasQuery.Result> {
  override val query: String = ALIAS_QUERY

  override val operationName: String = "AliasQuery"

  override fun responseType(): KClass<AliasQuery.Result> = AliasQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Query that accepts some input arguments
     */
    val first: Boolean,
    /**
     * Query that accepts some input arguments
     */
    val second: Boolean
  )
}
