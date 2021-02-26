package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.complexobjectquery.ComplexObject
import kotlin.String
import kotlin.reflect.KClass

const val COMPLEX_OBJECT_QUERY: String =
    "query ComplexObjectQuery {\n  complexObjectQuery {\n    id\n    name\n    optional\n    details {\n      id\n      flag\n      value\n    }\n  }\n}"

class ComplexObjectQuery : GraphQLClientRequest<ComplexObjectQuery.Result> {
  override val query: String = COMPLEX_OBJECT_QUERY

  override val operationName: String = "ComplexObjectQuery"

  override fun responseType(): KClass<ComplexObjectQuery.Result> = ComplexObjectQuery.Result::class

  data class Result(
    /**
     * Query returning an object that references another object
     */
    val complexObjectQuery: ComplexObject
  )
}
