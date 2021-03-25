package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.SimpleArgumentInput
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass

public const val JACKSON_INPUT_QUERY: String =
    "query JacksonInputQuery(${'$'}input: SimpleArgumentInput) {\n  inputObjectQuery(criteria: ${'$'}input)\n}"

public class JacksonInputQuery(
  public override val variables: JacksonInputQuery.Variables
) : GraphQLClientRequest<JacksonInputQuery.Result> {
  public override val query: String = JACKSON_INPUT_QUERY

  public override val operationName: String = "JacksonInputQuery"

  public override fun responseType(): KClass<JacksonInputQuery.Result> =
      JacksonInputQuery.Result::class

  public data class Variables(
    public val input: SimpleArgumentInput? = null
  )

  public data class Result(
    /**
     * Query that accepts some input arguments
     */
    public val inputObjectQuery: Boolean
  )
}
