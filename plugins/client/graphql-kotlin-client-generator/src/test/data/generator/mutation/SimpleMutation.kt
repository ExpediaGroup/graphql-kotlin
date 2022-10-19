package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.SimpleArgumentInput
import com.expediagroup.graphql.generated.simplemutation.BasicObject
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.reflect.KClass

public const val SIMPLE_MUTATION: String =
    "mutation SimpleMutation(${'$'}input: SimpleArgumentInput!) {\n  simpleMutation(update: ${'$'}input) {\n    id\n    name\n  }\n}"

@Generated
public class SimpleMutation(
  public override val variables: SimpleMutation.Variables,
) : GraphQLClientRequest<SimpleMutation.Result> {
  public override val query: String = SIMPLE_MUTATION

  public override val operationName: String = "SimpleMutation"

  public override fun responseType(): KClass<SimpleMutation.Result> = SimpleMutation.Result::class

  @Generated
  public data class Variables(
    @get:JsonProperty(value = "input")
    public val input: SimpleArgumentInput,
  )

  @Generated
  public data class Result(
    /**
     * Example of a muation
     */
    public val simpleMutation: BasicObject,
  )
}
