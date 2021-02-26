package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val HARD_CODED_INPUT_QUERY: String =
    "query HardCodedInputQuery {\n  inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n}"

@Serializable
class HardCodedInputQuery : GraphQLClientRequest<HardCodedInputQuery.Result> {
  override val query: String = HARD_CODED_INPUT_QUERY

  override val operationName: String = "HardCodedInputQuery"

  override fun responseType(): KClass<HardCodedInputQuery.Result> =
      HardCodedInputQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Query that accepts some input arguments
     */
    val inputObjectQuery: Boolean
  )
}
