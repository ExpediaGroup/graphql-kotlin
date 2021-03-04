package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.differentselectionsquery.ComplexObject
import com.expediagroup.graphql.generated.differentselectionsquery.ComplexObject2
import kotlin.String
import kotlin.reflect.KClass

const val DIFFERENT_SELECTIONS_QUERY: String =
    "query DifferentSelectionsQuery {\n  first: complexObjectQuery {\n    id\n    name\n  }\n  second: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n}"

class DifferentSelectionsQuery : GraphQLClientRequest<DifferentSelectionsQuery.Result> {
  override val query: String = DIFFERENT_SELECTIONS_QUERY

  override val operationName: String = "DifferentSelectionsQuery"

  override fun responseType(): KClass<DifferentSelectionsQuery.Result> =
      DifferentSelectionsQuery.Result::class

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
