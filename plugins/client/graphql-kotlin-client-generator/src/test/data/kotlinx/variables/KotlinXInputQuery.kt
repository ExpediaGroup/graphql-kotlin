package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.SimpleArgumentInput
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val KOTLIN_X_INPUT_QUERY: String =
    "query KotlinXInputQuery(${'$'}input: SimpleArgumentInput) {\n  inputObjectQuery(criteria: ${'$'}input)\n}"

@Serializable
class KotlinXInputQuery(
  override val variables: KotlinXInputQuery.Variables
) : GraphQLClientRequest<KotlinXInputQuery.Result> {
  override val query: String = KOTLIN_X_INPUT_QUERY

  override val operationName: String = "KotlinXInputQuery"

  override fun responseType(): KClass<KotlinXInputQuery.Result> = KotlinXInputQuery.Result::class

  @Serializable
  data class Variables(
    val input: SimpleArgumentInput? = null
  )

  @Serializable
  data class Result(
    /**
     * Query that accepts some input arguments
     */
    val inputObjectQuery: Boolean
  )
}
