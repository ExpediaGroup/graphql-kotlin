package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.unionwithfragmentssharingtypeconditionquery.BasicUnion
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.reflect.KClass

public const val UNION_WITH_FRAGMENTS_SHARING_TYPE_CONDITION_QUERY: String =
    "query UnionWithFragmentsSharingTypeConditionQuery {\n  unionQuery {\n    __typename\n    ...basicObjectIdFields\n    ...basicObjectNameFields\n    ...complexObjectIdFields\n    ...complexObjectOptionalFields\n  }\n}\n\nfragment basicObjectIdFields on BasicObject {\n  id\n}\n\nfragment basicObjectNameFields on BasicObject {\n  name\n}\n\nfragment complexObjectIdFields on ComplexObject {\n  id\n}\n\nfragment complexObjectOptionalFields on ComplexObject {\n  optional\n}"

@Generated
public class UnionWithFragmentsSharingTypeConditionQuery :
    GraphQLClientRequest<UnionWithFragmentsSharingTypeConditionQuery.Result> {
  override val query: String = UNION_WITH_FRAGMENTS_SHARING_TYPE_CONDITION_QUERY

  override val operationName: String = "UnionWithFragmentsSharingTypeConditionQuery"

  override fun responseType(): KClass<UnionWithFragmentsSharingTypeConditionQuery.Result> =
      UnionWithFragmentsSharingTypeConditionQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning union
     */
    @get:JsonProperty(value = "unionQuery")
    public val unionQuery: BasicUnion,
  )
}
