package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.SimpleArgumentInput
import com.expediagroup.graphql.generated.simplemutation.BasicObject
import kotlin.String
import kotlin.reflect.KClass

public const val SIMPLE_MUTATION: String =
    "mutation SimpleMutation(${'$'}input: SimpleArgumentInput!) {\n  simpleMutation(update: ${'$'}input) {\n    id\n    name\n  }\n}"

public class SimpleMutation(
  public override val variables: SimpleMutation.Variables
) : GraphQLClientRequest<SimpleMutation.Result> {
  public override val query: String = SIMPLE_MUTATION

  public override val operationName: String = "SimpleMutation"

  public override fun responseType(): KClass<SimpleMutation.Result> = SimpleMutation.Result::class

  public data class Variables(
    public val input: SimpleArgumentInput
  )

  public data class Result(
    /**
     * Example of a muation
     */
    public val simpleMutation: BasicObject
  )
}
