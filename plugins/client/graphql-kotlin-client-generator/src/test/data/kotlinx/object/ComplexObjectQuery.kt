package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.complexobjectquery.ComplexObject
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

public const val COMPLEX_OBJECT_QUERY: String =
    "query ComplexObjectQuery {\n  complexObjectQuery {\n    id\n    name\n    optional\n    details {\n      id\n      flag\n      value\n    }\n  }\n}"

@Generated
@Serializable
public class ComplexObjectQuery : GraphQLClientRequest<ComplexObjectQuery.Result> {
  @Required
  public override val query: String = COMPLEX_OBJECT_QUERY

  @Required
  public override val operationName: String = "ComplexObjectQuery"

  public override fun responseType(): KClass<ComplexObjectQuery.Result> =
      ComplexObjectQuery.Result::class

  @Generated
  @Serializable
  public data class Result(
    /**
     * Query returning an object that references another object
     */
    public val complexObjectQuery: ComplexObject,
  )
}
