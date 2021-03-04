package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.listquery.BasicObject
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

const val LIST_QUERY: String = "query ListQuery {\n  listQuery {\n    id\n    name\n  }\n}"

class ListQuery : GraphQLClientRequest<ListQuery.Result> {
  override val query: String = LIST_QUERY

  override val operationName: String = "ListQuery"

  override fun responseType(): KClass<ListQuery.Result> = ListQuery.Result::class

  data class Result(
    /**
     * Query returning list of simple objects
     */
    val listQuery: List<BasicObject>
  )
}
