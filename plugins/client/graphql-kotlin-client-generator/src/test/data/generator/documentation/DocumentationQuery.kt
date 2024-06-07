package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.documentationquery.DocObject
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.reflect.KClass

public const val DOCUMENTATION_QUERY: String =
    "query DocumentationQuery {\n  docQuery {\n    id\n  }\n}"

@Generated
public class DocumentationQuery : GraphQLClientRequest<DocumentationQuery.Result> {
  override val query: String = DOCUMENTATION_QUERY

  override val operationName: String = "DocumentationQuery"

  override fun responseType(): KClass<DocumentationQuery.Result> = DocumentationQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query to test doc strings
     */
    @get:JsonProperty(value = "docQuery")
    public val docQuery: DocObject,
  )
}
