package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.differentselectionsetquery.BasicUnion
import com.expediagroup.graphql.generated.differentselectionsetquery.BasicUnion2
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val DIFFERENT_SELECTION_SET_QUERY: String =
    "query DifferentSelectionSetQuery {\n  first: unionQuery {\n    __typename\n    ... on BasicObject {\n      id\n    }\n    ... on ComplexObject {\n      id\n    }\n  }\n  second: unionQuery {\n    __typename\n    ... on BasicObject {\n      name\n    }\n    ... on ComplexObject {\n      name\n    }\n  }\n}"

@Serializable
class DifferentSelectionSetQuery : GraphQLClientRequest<DifferentSelectionSetQuery.Result> {
  override val query: String = DIFFERENT_SELECTION_SET_QUERY

  override val operationName: String = "DifferentSelectionSetQuery"

  override fun responseType(): KClass<DifferentSelectionSetQuery.Result> =
      DifferentSelectionSetQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Query returning union
     */
    val first: BasicUnion,
    /**
     * Query returning union
     */
    val second: BasicUnion2
  )
}
