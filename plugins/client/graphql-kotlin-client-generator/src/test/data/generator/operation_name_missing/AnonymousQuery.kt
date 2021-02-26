package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.anonymousquery.ScalarWrapper
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val ANONYMOUS_QUERY: String = "query {\n  scalarQuery {\n    name\n  }\n}"

@Serializable
class AnonymousQuery : GraphQLClientRequest<AnonymousQuery.Result> {
  override val query: String = ANONYMOUS_QUERY

  override fun responseType(): KClass<AnonymousQuery.Result> = AnonymousQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    val scalarQuery: ScalarWrapper
  )
}
