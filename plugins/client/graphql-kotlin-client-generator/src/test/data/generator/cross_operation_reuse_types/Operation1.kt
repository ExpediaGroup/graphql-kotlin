package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.responses.ComplexObject
import com.expediagroup.graphql.generated.responses.ComplexObject2
import com.expediagroup.graphql.generated.responses.ComplexObject3
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.reflect.KClass

public const val OPERATION1: String =
    "query Operation1 {\n  first: complexObjectQuery {\n    id\n    name\n  }\n  second: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n  third: complexObjectQuery {\n    id\n    name\n    details {\n      id\n    }\n  }\n  fourth: complexObjectQuery {\n    id\n    name\n  }\n  fifth: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n}"

@Generated
public class Operation1 : GraphQLClientRequest<Operation1.Result> {
  override val query: String = OPERATION1

  override val operationName: String = "Operation1"

  override fun responseType(): KClass<Operation1.Result> = Operation1.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "first")
    public val first: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "second")
    public val second: ComplexObject2,
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "third")
    public val third: ComplexObject3,
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "fourth")
    public val fourth: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "fifth")
    public val fifth: ComplexObject2,
  )
}
