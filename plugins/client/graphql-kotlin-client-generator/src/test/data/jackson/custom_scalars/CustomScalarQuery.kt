package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.customscalarquery.ScalarWrapper
import kotlin.String
import kotlin.reflect.KClass

public const val CUSTOM_SCALAR_QUERY: String =
    "query CustomScalarQuery {\n  scalarQuery {\n    custom\n    customList\n    locale\n    listLocale\n  }\n}"

@Generated
public class CustomScalarQuery : GraphQLClientRequest<CustomScalarQuery.Result> {
  public override val query: String = CUSTOM_SCALAR_QUERY

  public override val operationName: String = "CustomScalarQuery"

  public override fun responseType(): KClass<CustomScalarQuery.Result> =
      CustomScalarQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    public val scalarQuery: ScalarWrapper,
  )
}
