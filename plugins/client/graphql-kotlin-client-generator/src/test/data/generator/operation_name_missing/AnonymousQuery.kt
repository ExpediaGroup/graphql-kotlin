package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.anonymousquery.ScalarWrapper
import kotlin.String
import kotlin.reflect.KClass

public const val ANONYMOUS_QUERY: String = "query {\n  scalarQuery {\n    name\n  }\n}"

@Generated
public class AnonymousQuery : GraphQLClientRequest<AnonymousQuery.Result> {
  public override val query: String = ANONYMOUS_QUERY

  public override fun responseType(): KClass<AnonymousQuery.Result> = AnonymousQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    public val scalarQuery: ScalarWrapper,
  )
}
