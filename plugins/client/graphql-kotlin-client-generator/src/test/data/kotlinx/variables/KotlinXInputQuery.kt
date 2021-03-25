package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.SimpleArgumentInput
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

public const val KOTLIN_X_INPUT_QUERY: String =
    "query KotlinXInputQuery(${'$'}input: SimpleArgumentInput) {\n  inputObjectQuery(criteria: ${'$'}input)\n}"

@Serializable
public class KotlinXInputQuery(
  public override val variables: KotlinXInputQuery.Variables
) : GraphQLClientRequest<KotlinXInputQuery.Result> {
  public override val query: String = KOTLIN_X_INPUT_QUERY

  public override val operationName: String = "KotlinXInputQuery"

  public override fun responseType(): KClass<KotlinXInputQuery.Result> =
      KotlinXInputQuery.Result::class

  @Serializable
  public data class Variables(
    public val input: SimpleArgumentInput? = null
  )

  @Serializable
  public data class Result(
    /**
     * Query that accepts some input arguments
     */
    public val inputObjectQuery: Boolean
  )
}
