package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.interfacewithinlinefragmentsquery.BasicInterface
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

public const val INTERFACE_WITH_INLINE_FRAGMENTS_QUERY: String =
    "query InterfaceWithInlineFragmentsQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n}"

@Serializable
public class InterfaceWithInlineFragmentsQuery :
    GraphQLClientRequest<InterfaceWithInlineFragmentsQuery.Result> {
  public override val query: String = INTERFACE_WITH_INLINE_FRAGMENTS_QUERY

  public override val operationName: String = "InterfaceWithInlineFragmentsQuery"

  public override fun responseType(): KClass<InterfaceWithInlineFragmentsQuery.Result> =
      InterfaceWithInlineFragmentsQuery.Result::class

  @Serializable
  public data class Result(
    /**
     * Query returning an interface
     */
    public val interfaceQuery: BasicInterface
  )
}
