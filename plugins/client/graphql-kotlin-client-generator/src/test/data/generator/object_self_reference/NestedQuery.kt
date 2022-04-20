package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.nestedquery.NestedObject
import kotlin.String
import kotlin.reflect.KClass

public const val NESTED_QUERY: String =
    "query NestedQuery {\n  nestedObjectQuery {\n    id\n    name\n    children {\n      name\n      children {\n        id\n        name\n        children {\n          id\n          name\n        }\n      }\n    }\n  }\n}"

@Generated
public class NestedQuery : GraphQLClientRequest<NestedQuery.Result> {
  public override val query: String = NESTED_QUERY

  public override val operationName: String = "NestedQuery"

  public override fun responseType(): KClass<NestedQuery.Result> = NestedQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning object referencing itself
     */
    public val nestedObjectQuery: NestedObject,
  )
}
