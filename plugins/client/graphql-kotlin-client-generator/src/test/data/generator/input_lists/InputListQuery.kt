package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val INPUT_LIST_QUERY: String =
    "query InputListQuery(${'$'}nullableIds: [String], ${'$'}nullableIdList: [String!], ${'$'}nonNullableIds: [String!]!) {\n  listInputQuery(nullableIds: ${'$'}nulllableIds, nonNullableIds: ${'$'}nonNullableIds)\n}"

@Generated
public class InputListQuery(
  public override val variables: InputListQuery.Variables
) : GraphQLClientRequest<InputListQuery.Result> {
  public override val query: String = INPUT_LIST_QUERY

  public override val operationName: String = "InputListQuery"

  public override fun responseType(): KClass<InputListQuery.Result> = InputListQuery.Result::class

  @Generated
  public data class Variables(
    public val nullableIds: List<String?>? = null,
    public val nullableIdList: List<String>? = null,
    public val nonNullableIds: List<String>
  )

  @Generated
  public data class Result(
    /**
     * Query accepting list input
     */
    public val listInputQuery: String?
  )
}
