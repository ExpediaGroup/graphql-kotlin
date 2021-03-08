package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.Deprecated
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val DEPRECATED_OPT_IN_QUERY: String = "query DeprecatedOptInQuery {\n  deprecatedQuery\n}"

@Serializable
class DeprecatedOptInQuery : GraphQLClientRequest<DeprecatedOptInQuery.Result> {
  override val query: String = DEPRECATED_OPT_IN_QUERY

  override val operationName: String = "DeprecatedOptInQuery"

  override fun responseType(): KClass<DeprecatedOptInQuery.Result> =
      DeprecatedOptInQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Deprecated query that should not be used anymore
     */
    @Deprecated(message = "old query should not be used")
    val deprecatedQuery: String
  )
}
