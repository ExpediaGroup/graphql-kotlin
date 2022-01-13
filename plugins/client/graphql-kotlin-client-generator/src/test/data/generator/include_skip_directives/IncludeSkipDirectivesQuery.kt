package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.enums.CustomEnum
import com.expediagroup.graphql.generated.includeskipdirectivesquery.ScalarWrapper
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass

public const val INCLUDE_SKIP_DIRECTIVES_QUERY: String =
    "query IncludeSkipDirectivesQuery(${'$'}includeCondition: Boolean!, ${'$'}skipCondition: Boolean!) {\n  enumQuery @include(if: ${'$'}includeCondition)\n  scalarQuery @skip(if: ${'$'}skipCondition) {\n    count\n  }\n}"

@Generated
public class IncludeSkipDirectivesQuery(
  public override val variables: IncludeSkipDirectivesQuery.Variables
) : GraphQLClientRequest<IncludeSkipDirectivesQuery.Result> {
  public override val query: String = INCLUDE_SKIP_DIRECTIVES_QUERY

  public override val operationName: String = "IncludeSkipDirectivesQuery"

  public override fun responseType(): KClass<IncludeSkipDirectivesQuery.Result> =
      IncludeSkipDirectivesQuery.Result::class

  @Generated
  public data class Variables(
    public val includeCondition: Boolean,
    public val skipCondition: Boolean
  )

  @Generated
  public data class Result(
    /**
     * Query that returns enum value
     */
    public val enumQuery: CustomEnum?,
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    public val scalarQuery: ScalarWrapper?
  )
}
