package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.documentationquery.DocObject
import kotlin.String
import kotlin.reflect.KClass

public const val DOCUMENTATION_QUERY: String =
    "query DocumentationQuery {\n  docQuery {\n    id\n  }\n}"

public class DocumentationQuery : GraphQLClientRequest<DocumentationQuery.Result> {
  public override val query: String = DOCUMENTATION_QUERY

  public override val operationName: String = "DocumentationQuery"

  public override fun responseType(): KClass<DocumentationQuery.Result> =
      DocumentationQuery.Result::class

  public data class Result(
    /**
     * Query to test doc strings
     */
    public val docQuery: DocObject
  )
}
