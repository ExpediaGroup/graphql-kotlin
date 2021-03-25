package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.Deprecated
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

public const val DEPRECATED_OPT_IN_QUERY: String =
    "query DeprecatedOptInQuery {\n  deprecatedQuery\n}"

@Serializable
public class DeprecatedOptInQuery : GraphQLClientRequest<DeprecatedOptInQuery.Result> {
  public override val query: String = DEPRECATED_OPT_IN_QUERY

  public override val operationName: String = "DeprecatedOptInQuery"

  public override fun responseType(): KClass<DeprecatedOptInQuery.Result> =
      DeprecatedOptInQuery.Result::class

  @Serializable
  public data class Result(
    /**
     * Deprecated query that should not be used anymore
     */
    @Deprecated(message = "old query should not be used")
    public val deprecatedQuery: String
  )
}
