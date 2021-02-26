package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.SimpleArgumentInput
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass

const val JACKSON_INPUT_QUERY: String =
    "query JacksonInputQuery(${'$'}input: SimpleArgumentInput) {\n  inputObjectQuery(criteria: ${'$'}input)\n}"

class JacksonInputQuery(
  override val variables: JacksonInputQuery.Variables
) : GraphQLClientRequest<JacksonInputQuery.Result> {
  override val query: String = JACKSON_INPUT_QUERY

  override val operationName: String = "JacksonInputQuery"

  override fun responseType(): KClass<JacksonInputQuery.Result> = JacksonInputQuery.Result::class

  data class Variables(
    val input: SimpleArgumentInput? = null
  )

  data class Result(
    /**
     * Query that accepts some input arguments
     */
    val inputObjectQuery: Boolean
  )
}
