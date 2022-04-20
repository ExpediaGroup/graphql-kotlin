package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.differentsubselectionquery.ComplexObject
import com.expediagroup.graphql.generated.differentsubselectionquery.ComplexObject2
import kotlin.String
import kotlin.reflect.KClass

public const val DIFFERENT_SUBSELECTION_QUERY: String =
    "query DifferentSubselectionQuery {\n  first: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n      flag\n    }\n  }\n  second: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n}"

@Generated
public class DifferentSubselectionQuery : GraphQLClientRequest<DifferentSubselectionQuery.Result> {
  public override val query: String = DIFFERENT_SUBSELECTION_QUERY

  public override val operationName: String = "DifferentSubselectionQuery"

  public override fun responseType(): KClass<DifferentSubselectionQuery.Result> =
      DifferentSubselectionQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning an object that references another object
     */
    public val first: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    public val second: ComplexObject2,
  )
}
