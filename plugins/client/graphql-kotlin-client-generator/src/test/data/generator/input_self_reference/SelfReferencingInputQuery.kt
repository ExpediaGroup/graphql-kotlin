package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.ComplexArgumentInput
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val SELF_REFERENCING_INPUT_QUERY: String =
    "query SelfReferencingInputQuery(${'$'}input: ComplexArgumentInput) {\n  complexInputObjectQuery(criteria: ${'$'}input)\n}"

@Serializable
class SelfReferencingInputQuery(
  override val variables: SelfReferencingInputQuery.Variables
) : GraphQLClientRequest<SelfReferencingInputQuery.Result> {
  override val query: String = SELF_REFERENCING_INPUT_QUERY

  override val operationName: String = "SelfReferencingInputQuery"

  override fun responseType(): KClass<SelfReferencingInputQuery.Result> =
      SelfReferencingInputQuery.Result::class

  @Serializable
  data class Variables(
    val input: ComplexArgumentInput? = null
  )

  @Serializable
  data class Result(
    /**
     * Query that accepts self referencing input object
     */
    val complexInputObjectQuery: Boolean
  )
}
