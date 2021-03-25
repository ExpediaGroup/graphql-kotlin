package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass

public const val HARD_CODED_INPUT_QUERY: String =
    "query HardCodedInputQuery {\n  inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n}"

public class HardCodedInputQuery : GraphQLClientRequest<HardCodedInputQuery.Result> {
  public override val query: String = HARD_CODED_INPUT_QUERY

  public override val operationName: String = "HardCodedInputQuery"

  public override fun responseType(): KClass<HardCodedInputQuery.Result> =
      HardCodedInputQuery.Result::class

  public data class Result(
    /**
     * Query that accepts some input arguments
     */
    public val inputObjectQuery: Boolean
  )
}
