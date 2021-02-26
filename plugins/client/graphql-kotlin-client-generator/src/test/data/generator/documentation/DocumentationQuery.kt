package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.documentationquery.DocObject
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val DOCUMENTATION_QUERY: String = "query DocumentationQuery {\n  docQuery {\n    id\n  }\n}"

@Serializable
class DocumentationQuery : GraphQLClientRequest<DocumentationQuery.Result> {
  override val query: String = DOCUMENTATION_QUERY

  override val operationName: String = "DocumentationQuery"

  override fun responseType(): KClass<DocumentationQuery.Result> = DocumentationQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Query to test doc strings
     */
    val docQuery: DocObject
  )
}
