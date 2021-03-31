package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.enums.CustomEnum
import com.expediagroup.graphql.generated.enums.CustomEnum.__UNKNOWN_VALUE
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

public const val ENUM_QUERY: String = "query EnumQuery {\n  enumQuery\n}"

@Serializable
public class EnumQuery : GraphQLClientRequest<EnumQuery.Result> {
  public override val query: String = ENUM_QUERY

  public override val operationName: String = "EnumQuery"

  public override fun responseType(): KClass<EnumQuery.Result> = EnumQuery.Result::class

  @Serializable
  public data class Result(
    /**
     * Query that returns enum value
     */
    public val enumQuery: CustomEnum = CustomEnum.__UNKNOWN_VALUE
  )
}
