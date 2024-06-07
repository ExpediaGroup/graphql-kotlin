package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.scalaraliasquery.ScalarWrapper
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.reflect.KClass

public const val SCALAR_ALIAS_QUERY: String =
    "query ScalarAliasQuery {\n  scalarQuery {\n    id\n    custom\n  }\n}"

@Generated
public class ScalarAliasQuery : GraphQLClientRequest<ScalarAliasQuery.Result> {
  override val query: String = SCALAR_ALIAS_QUERY

  override val operationName: String = "ScalarAliasQuery"

  override fun responseType(): KClass<ScalarAliasQuery.Result> = ScalarAliasQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    @get:JsonProperty(value = "scalarQuery")
    public val scalarQuery: ScalarWrapper,
  )
}
