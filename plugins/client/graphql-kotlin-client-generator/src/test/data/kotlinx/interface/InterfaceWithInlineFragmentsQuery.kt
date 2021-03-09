package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.interfacewithinlinefragmentsquery.BasicInterface
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val INTERFACE_WITH_INLINE_FRAGMENTS_QUERY: String =
    "query InterfaceWithInlineFragmentsQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n}"

@Serializable
class InterfaceWithInlineFragmentsQuery :
    GraphQLClientRequest<InterfaceWithInlineFragmentsQuery.Result> {
  override val query: String = INTERFACE_WITH_INLINE_FRAGMENTS_QUERY

  override val operationName: String = "InterfaceWithInlineFragmentsQuery"

  override fun responseType(): KClass<InterfaceWithInlineFragmentsQuery.Result> =
      InterfaceWithInlineFragmentsQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Query returning an interface
     */
    val interfaceQuery: BasicInterface
  )
}
