package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.aliasnestedquery.ComplexObject
import com.expediagroup.graphql.generated.aliasnestedquery.ComplexObject2
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.reflect.KClass

public const val ALIAS_NESTED_QUERY: String =
    "query AliasNestedQuery {\n  first: complexObjectQuery {\n    nameA: name\n  }\n  second: complexObjectQuery {\n    nameB: name\n  }\n}"

@Generated
public class AliasNestedQuery : GraphQLClientRequest<AliasNestedQuery.Result> {
  override val query: String = ALIAS_NESTED_QUERY

  override val operationName: String = "AliasNestedQuery"

  override fun responseType(): KClass<AliasNestedQuery.Result> = AliasNestedQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "first")
    public val first: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "second")
    public val second: ComplexObject2,
  )
}
