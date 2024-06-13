package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.complexobjectquery.ComplexObject
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.reflect.KClass

public const val COMPLEX_OBJECT_QUERY: String =
    "query ComplexObjectQuery {\n  complexObjectQuery {\n    id\n    name\n    optional\n    details {\n      id\n      flag\n      value\n    }\n  }\n}"

@Generated
public class ComplexObjectQuery : GraphQLClientRequest<ComplexObjectQuery.Result> {
  override val query: String = COMPLEX_OBJECT_QUERY

  override val operationName: String = "ComplexObjectQuery"

  override fun responseType(): KClass<ComplexObjectQuery.Result> = ComplexObjectQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "complexObjectQuery")
    public val complexObjectQuery: ComplexObject,
  )
}
