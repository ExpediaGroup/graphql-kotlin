package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass

public const val ALIAS_QUERY: String =
    "query AliasQuery {\n  first: inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n  second: inputObjectQuery(criteria: { min: 5.0, max: 10.0 } )\n}"

@Generated
public class AliasQuery : GraphQLClientRequest<AliasQuery.Result> {
  override val query: String = ALIAS_QUERY

  override val operationName: String = "AliasQuery"

  override fun responseType(): KClass<AliasQuery.Result> = AliasQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query that accepts some input arguments
     */
    @get:JsonProperty(value = "first")
    public val first: Boolean,
    /**
     * Query that accepts some input arguments
     */
    @get:JsonProperty(value = "second")
    public val second: Boolean,
  )
}
