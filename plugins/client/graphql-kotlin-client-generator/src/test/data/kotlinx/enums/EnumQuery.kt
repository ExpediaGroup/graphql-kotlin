package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.enums.CustomEnum
import com.expediagroup.graphql.generated.enums.OtherEnum
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

public const val ENUM_QUERY: String = "query EnumQuery {\n  enumQuery\n  otherEnumQuery\n}"

@Generated
@Serializable
public class EnumQuery : GraphQLClientRequest<EnumQuery.Result> {
  @Required
  public override val query: String = ENUM_QUERY

  @Required
  public override val operationName: String = "EnumQuery"

  public override fun responseType(): KClass<EnumQuery.Result> = EnumQuery.Result::class

  @Generated
  @Serializable
  public data class Result(
    /**
     * Query that returns enum value
     */
    public val enumQuery: CustomEnum = CustomEnum.__UNKNOWN_VALUE,
    /**
     * Query that returns other enum value
     */
    public val otherEnumQuery: OtherEnum = OtherEnum.__UNKNOWN_VALUE,
  )
}
