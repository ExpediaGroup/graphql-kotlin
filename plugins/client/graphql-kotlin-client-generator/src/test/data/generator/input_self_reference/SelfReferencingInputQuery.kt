package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.ComplexArgumentInput
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass

public const val SELF_REFERENCING_INPUT_QUERY: String =
    "query SelfReferencingInputQuery(${'$'}input: ComplexArgumentInput) {\n  complexInputObjectQuery(criteria: ${'$'}input)\n}"

@Generated
public class SelfReferencingInputQuery(
  override val variables: SelfReferencingInputQuery.Variables,
) : GraphQLClientRequest<SelfReferencingInputQuery.Result> {
  override val query: String = SELF_REFERENCING_INPUT_QUERY

  override val operationName: String = "SelfReferencingInputQuery"

  override fun responseType(): KClass<SelfReferencingInputQuery.Result> =
      SelfReferencingInputQuery.Result::class

  @Generated
  public data class Variables(
    @get:JsonProperty(value = "input")
    public val input: ComplexArgumentInput? = null,
  )

  @Generated
  public data class Result(
    /**
     * Query that accepts self referencing input object
     */
    @get:JsonProperty(value = "complexInputObjectQuery")
    public val complexInputObjectQuery: Boolean,
  )
}
