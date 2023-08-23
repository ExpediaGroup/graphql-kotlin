package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.unionmissingtypeselection.BasicUnion
import kotlin.String
import kotlin.reflect.KClass

public const val UNION_MISSING_TYPE_SELECTION: String =
    "query UnionMissingTypeSelection {\n  unionQuery {\n    ... on BasicObject {\n      __typename\n      id\n      name\n    }\n  }\n}"

@Generated
public class UnionMissingTypeSelection : GraphQLClientRequest<UnionMissingTypeSelection.Result> {
  override val query: String = UNION_MISSING_TYPE_SELECTION

  override val operationName: String = "UnionMissingTypeSelection"

  override fun responseType(): KClass<UnionMissingTypeSelection.Result> =
      UnionMissingTypeSelection.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning union
     */
    public val unionQuery: BasicUnion,
  )
}
