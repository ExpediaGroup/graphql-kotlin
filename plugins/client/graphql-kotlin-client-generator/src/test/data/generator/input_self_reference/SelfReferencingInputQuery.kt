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
  public override val variables: SelfReferencingInputQuery.Variables,
) : GraphQLClientRequest<SelfReferencingInputQuery.Result> {
  public override val query: String = SELF_REFERENCING_INPUT_QUERY

  public override val operationName: String = "SelfReferencingInputQuery"

  public override fun responseType(): KClass<SelfReferencingInputQuery.Result> =
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
    public val complexInputObjectQuery: Boolean,
  )
}
