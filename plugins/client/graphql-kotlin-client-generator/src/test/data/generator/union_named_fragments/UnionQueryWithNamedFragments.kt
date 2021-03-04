package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.unionquerywithnamedfragments.BasicUnion
import kotlin.String
import kotlin.reflect.KClass

const val UNION_QUERY_WITH_NAMED_FRAGMENTS: String =
    "query UnionQueryWithNamedFragments {\n  unionQuery {\n    ... basicObjectFields\n    ... complexObjectFields\n  }\n}\n\nfragment basicObjectFields on BasicObject {\n  __typename\n  id\n  name\n}\nfragment complexObjectFields on ComplexObject {\n  __typename\n  id\n  name\n  optional\n}"

class UnionQueryWithNamedFragments : GraphQLClientRequest<UnionQueryWithNamedFragments.Result> {
  override val query: String = UNION_QUERY_WITH_NAMED_FRAGMENTS

  override val operationName: String = "UnionQueryWithNamedFragments"

  override fun responseType(): KClass<UnionQueryWithNamedFragments.Result> =
      UnionQueryWithNamedFragments.Result::class

  data class Result(
    /**
     * Query returning union
     */
    val unionQuery: BasicUnion
  )
}
