package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.listquery.BasicObject
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val LIST_QUERY: String = "query ListQuery {\n  listQuery {\n    id\n    name\n  }\n}"

@Generated
public class ListQuery : GraphQLClientRequest<ListQuery.Result> {
  override val query: String = LIST_QUERY

  override val operationName: String = "ListQuery"

  override fun responseType(): KClass<ListQuery.Result> = ListQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning list of simple objects
     */
    @get:JsonProperty(value = "listQuery")
    public val listQuery: List<BasicObject>,
  )
}
