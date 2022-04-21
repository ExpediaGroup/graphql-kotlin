package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.SimpleArgumentInput
import com.expediagroup.graphql.generated.scalars.OptionalSimpleArgumentInputSerializer
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

public const val KOTLIN_X_INPUT_QUERY: String =
    "query KotlinXInputQuery(${'$'}input: SimpleArgumentInput) {\n  inputObjectQuery(criteria: ${'$'}input)\n}"

@Generated
@Serializable
public class KotlinXInputQuery(
  public override val variables: KotlinXInputQuery.Variables,
) : GraphQLClientRequest<KotlinXInputQuery.Result> {
  @Required
  public override val query: String = KOTLIN_X_INPUT_QUERY

  @Required
  public override val operationName: String = "KotlinXInputQuery"

  public override fun responseType(): KClass<KotlinXInputQuery.Result> =
      KotlinXInputQuery.Result::class

  @Generated
  @Serializable
  public data class Variables(
    @Serializable(with = OptionalSimpleArgumentInputSerializer::class)
    public val input: OptionalInput<SimpleArgumentInput> = OptionalInput.Undefined,
  )

  @Generated
  @Serializable
  public data class Result(
    /**
     * Query that accepts some input arguments
     */
    public val inputObjectQuery: Boolean,
  )
}
