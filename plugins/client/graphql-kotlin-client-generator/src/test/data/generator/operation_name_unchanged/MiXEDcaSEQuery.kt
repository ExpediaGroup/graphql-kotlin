package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.mixedcasequery.ScalarWrapper
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.reflect.KClass

public const val MI_XE_DCA_SE_QUERY: String =
    "query miXEDcaSEQuery {\n  scalarQuery {\n    name\n  }\n}"

@Generated
public class MiXEDcaSEQuery : GraphQLClientRequest<MiXEDcaSEQuery.Result> {
  override val query: String = MI_XE_DCA_SE_QUERY

  override val operationName: String = "miXEDcaSEQuery"

  override fun responseType(): KClass<MiXEDcaSEQuery.Result> = MiXEDcaSEQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    @get:JsonProperty(value = "scalarQuery")
    public val scalarQuery: ScalarWrapper,
  )
}
