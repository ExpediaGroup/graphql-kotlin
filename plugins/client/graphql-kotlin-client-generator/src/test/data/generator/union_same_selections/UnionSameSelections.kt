package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.unionsameselections.ProductRatingSupportingMessage
import com.expediagroup.graphql.generated.unionsameselections.ProductSupportingMessage
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val UNION_SAME_SELECTIONS: String =
    "query UnionSameSelections {\n  message1 {\n    __typename\n    ... on ProductRatingLink {\n      link {\n        text\n      }\n      action {\n        text\n      }\n    }\n    ... on EGDSPlainText {\n      text\n    }\n  }\n  message2 {\n    __typename\n    ... on EGDSParagraph {\n      text\n    }\n    ... on EGDSPlainText {\n      text\n    }\n  }\n}"

@Generated
public class UnionSameSelections : GraphQLClientRequest<UnionSameSelections.Result> {
  public override val query: String = UNION_SAME_SELECTIONS

  public override val operationName: String = "UnionSameSelections"

  public override fun responseType(): KClass<UnionSameSelections.Result> =
      UnionSameSelections.Result::class

  @Generated
  public data class Result(
    public val message1: List<ProductRatingSupportingMessage>,
    public val message2: List<ProductSupportingMessage>,
  )
}
