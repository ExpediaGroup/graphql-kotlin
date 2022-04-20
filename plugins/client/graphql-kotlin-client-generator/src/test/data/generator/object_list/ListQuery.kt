package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.listquery.BasicObject
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val LIST_QUERY: String = "query ListQuery {\n  listQuery {\n    id\n    name\n  }\n}"

@Generated
public class ListQuery : GraphQLClientRequest<ListQuery.Result> {
  public override val query: String = LIST_QUERY

  public override val operationName: String = "ListQuery"

  public override fun responseType(): KClass<ListQuery.Result> = ListQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning list of simple objects
     */
    public val listQuery: List<BasicObject>,
  )
}
