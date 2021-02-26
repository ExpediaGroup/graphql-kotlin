package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.differentselectionsetquery.BasicUnion
import com.expediagroup.graphql.generated.differentselectionsetquery.ComplexObject2
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val DIFFERENT_SELECTION_SET_QUERY: String =
    "query DifferentSelectionSetQuery {\n  unionQuery {\n    __typename\n    ... on BasicObject {\n      id\n      name\n    }\n    ... on ComplexObject {\n      id\n      name\n      optional\n    }\n  }\n  complexObjectQuery {\n    id\n    name\n    details {\n      value\n    }\n  }\n}"

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
    val unionQuery: BasicUnion,
    /**
     * Query returning an object that references another object
     */
    val complexObjectQuery: ComplexObject2
  )
}
