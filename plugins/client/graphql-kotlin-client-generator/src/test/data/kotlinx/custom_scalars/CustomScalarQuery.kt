package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.customscalarquery.ScalarWrapper
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

public const val CUSTOM_SCALAR_QUERY: String =
    "query CustomScalarQuery {\n  first: scalarQuery {\n    ... scalarSelections\n  }\n  second: scalarQuery {\n    ... scalarSelections\n  }\n}\nfragment scalarSelections on ScalarWrapper {\n  id\n  custom\n  customList\n  locale\n  listLocale\n}"

@Generated
@Serializable
public class CustomScalarQuery : GraphQLClientRequest<CustomScalarQuery.Result> {
  @Required
  public override val query: String = CUSTOM_SCALAR_QUERY

  @Required
  public override val operationName: String = "CustomScalarQuery"

  public override fun responseType(): KClass<CustomScalarQuery.Result> =
      CustomScalarQuery.Result::class

  @Generated
  @Serializable
  public data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    public val first: ScalarWrapper,
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    public val second: ScalarWrapper,
  )
}
