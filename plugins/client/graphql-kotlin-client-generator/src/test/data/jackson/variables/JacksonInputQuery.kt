package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.SimpleArgumentInput
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass

public const val JACKSON_INPUT_QUERY: String =
    "query JacksonInputQuery(${'$'}input: SimpleArgumentInput) {\n  inputObjectQuery(criteria: ${'$'}input)\n}"

@Generated
public class JacksonInputQuery(
  public override val variables: JacksonInputQuery.Variables,
) : GraphQLClientRequest<JacksonInputQuery.Result> {
  public override val query: String = JACKSON_INPUT_QUERY

  public override val operationName: String = "JacksonInputQuery"

  public override fun responseType(): KClass<JacksonInputQuery.Result> =
      JacksonInputQuery.Result::class

  @Generated
  public data class Variables(
    @get:JsonProperty(value = "input")
    public val input: OptionalInput<SimpleArgumentInput> = OptionalInput.Undefined,
  )

  @Generated
  public data class Result(
    /**
     * Query that accepts some input arguments
     */
    public val inputObjectQuery: Boolean,
  )
}
