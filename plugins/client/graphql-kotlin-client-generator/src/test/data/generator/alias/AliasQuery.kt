package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass

public const val ALIAS_QUERY: String =
    "query AliasQuery {\n  first: inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n  second: inputObjectQuery(criteria: { min: 5.0, max: 10.0 } )\n}"

@Generated
public class AliasQuery : GraphQLClientRequest<AliasQuery.Result> {
  public override val query: String = ALIAS_QUERY

  public override val operationName: String = "AliasQuery"

  public override fun responseType(): KClass<AliasQuery.Result> = AliasQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query that accepts some input arguments
     */
    public val first: Boolean,
    /**
     * Query that accepts some input arguments
     */
    public val second: Boolean,
  )
}
