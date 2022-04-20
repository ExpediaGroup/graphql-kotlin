package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.Deprecated
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

public const val DEPRECATED_OPT_IN_QUERY: String =
    "query DeprecatedOptInQuery {\n  deprecatedQuery\n}"

@Generated
@Serializable
public class DeprecatedOptInQuery : GraphQLClientRequest<DeprecatedOptInQuery.Result> {
  @Required
  public override val query: String = DEPRECATED_OPT_IN_QUERY

  @Required
  public override val operationName: String = "DeprecatedOptInQuery"

  public override fun responseType(): KClass<DeprecatedOptInQuery.Result> =
      DeprecatedOptInQuery.Result::class

  @Generated
  @Serializable
  public data class Result(
    /**
     * Deprecated query that should not be used anymore
     */
    @Deprecated(message = "old query should not be used")
    public val deprecatedQuery: String,
  )
}
