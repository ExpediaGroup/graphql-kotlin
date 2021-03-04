package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.nestedquery.NestedObject
import kotlin.String
import kotlin.reflect.KClass

const val NESTED_QUERY: String =
    "query NestedQuery {\n  nestedObjectQuery {\n    id\n    name\n    children {\n      name\n      children {\n        id\n        name\n        children {\n          id\n          name\n        }\n      }\n    }\n  }\n}"

class NestedQuery : GraphQLClientRequest<NestedQuery.Result> {
  override val query: String = NESTED_QUERY

  override val operationName: String = "NestedQuery"

  override fun responseType(): KClass<NestedQuery.Result> = NestedQuery.Result::class

  data class Result(
    /**
     * Query returning object referencing itself
     */
    val nestedObjectQuery: NestedObject
  )
}
