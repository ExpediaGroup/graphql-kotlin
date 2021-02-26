package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.differentsubselectionquery.ComplexObject
import com.expediagroup.graphql.generated.differentsubselectionquery.ComplexObject2
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val DIFFERENT_SUBSELECTION_QUERY: String =
    "query DifferentSubselectionQuery {\n  first: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n      flag\n    }\n  }\n  second: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n}"

@Serializable
class DifferentSubselectionQuery : GraphQLClientRequest<DifferentSubselectionQuery.Result> {
  override val query: String = DIFFERENT_SUBSELECTION_QUERY

  override val operationName: String = "DifferentSubselectionQuery"

  override fun responseType(): KClass<DifferentSubselectionQuery.Result> =
      DifferentSubselectionQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Query returning an object that references another object
     */
    val first: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    val second: ComplexObject2
  )
}
