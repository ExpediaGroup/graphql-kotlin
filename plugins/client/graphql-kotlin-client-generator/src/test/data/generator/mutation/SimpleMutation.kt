package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.SimpleArgumentInput
import com.expediagroup.graphql.generated.simplemutation.BasicObject
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val SIMPLE_MUTATION: String =
    "mutation SimpleMutation(${'$'}input: SimpleArgumentInput!) {\n  simpleMutation(update: ${'$'}input) {\n    id\n    name\n  }\n}"

@Serializable
class SimpleMutation(
  override val variables: SimpleMutation.Variables
) : GraphQLClientRequest<SimpleMutation.Result> {
  override val query: String = SIMPLE_MUTATION

  override val operationName: String = "SimpleMutation"

  override fun responseType(): KClass<SimpleMutation.Result> = SimpleMutation.Result::class

  @Serializable
  data class Variables(
    val input: SimpleArgumentInput
  )

  @Serializable
  data class Result(
    /**
     * Example of a muation
     */
    val simpleMutation: BasicObject
  )
}
