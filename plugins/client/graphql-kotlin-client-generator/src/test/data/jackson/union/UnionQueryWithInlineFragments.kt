package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.unionquerywithinlinefragments.BasicUnion
import kotlin.String
import kotlin.reflect.KClass

public const val UNION_QUERY_WITH_INLINE_FRAGMENTS: String =
    "query UnionQueryWithInlineFragments {\n  unionQuery {\n    __typename\n    ... on BasicObject {\n      id\n      name\n    }\n    ... on ComplexObject {\n      id\n      name\n      optional\n    }\n  }\n}"

@Generated
public class UnionQueryWithInlineFragments :
    GraphQLClientRequest<UnionQueryWithInlineFragments.Result> {
  public override val query: String = UNION_QUERY_WITH_INLINE_FRAGMENTS

  public override val operationName: String = "UnionQueryWithInlineFragments"

  public override fun responseType(): KClass<UnionQueryWithInlineFragments.Result> =
      UnionQueryWithInlineFragments.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning union
     */
    public val unionQuery: BasicUnion,
  )
}
