package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass

public const val HARD_CODED_INPUT_QUERY: String =
    "query HardCodedInputQuery {\n  inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n}"

@Generated
public class HardCodedInputQuery : GraphQLClientRequest<HardCodedInputQuery.Result> {
  override val query: String = HARD_CODED_INPUT_QUERY

  override val operationName: String = "HardCodedInputQuery"

  override fun responseType(): KClass<HardCodedInputQuery.Result> =
      HardCodedInputQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query that accepts some input arguments
     */
    @get:JsonProperty(value = "inputObjectQuery")
    public val inputObjectQuery: Boolean,
  )
}
