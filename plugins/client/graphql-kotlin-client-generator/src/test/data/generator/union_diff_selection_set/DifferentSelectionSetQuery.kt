package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.differentselectionsetquery.BasicUnion
import com.expediagroup.graphql.generated.differentselectionsetquery.BasicUnion2
import kotlin.String
import kotlin.reflect.KClass

public const val DIFFERENT_SELECTION_SET_QUERY: String =
    "query DifferentSelectionSetQuery {\n  first: unionQuery {\n    __typename\n    ... on BasicObject {\n      id\n    }\n    ... on ComplexObject {\n      id\n    }\n  }\n  second: unionQuery {\n    __typename\n    ... on BasicObject {\n      name\n    }\n    ... on ComplexObject {\n      name\n    }\n  }\n}"

@Generated
public class DifferentSelectionSetQuery : GraphQLClientRequest<DifferentSelectionSetQuery.Result> {
  public override val query: String = DIFFERENT_SELECTION_SET_QUERY

  public override val operationName: String = "DifferentSelectionSetQuery"

  public override fun responseType(): KClass<DifferentSelectionSetQuery.Result> =
      DifferentSelectionSetQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning union
     */
    public val first: BasicUnion,
    /**
     * Query returning union
     */
    public val second: BasicUnion2,
  )
}
