package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.interfacewithnamedfragmentsquery.BasicInterface
import kotlin.String
import kotlin.reflect.KClass

public const val INTERFACE_WITH_NAMED_FRAGMENTS_QUERY: String =
    "query InterfaceWithNamedFragmentsQuery {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... firstInterfaceImplFields\n    ... secondInterfaceImplFields\n  }\n}\n\nfragment firstInterfaceImplFields on FirstInterfaceImplementation {\n  id\n  name\n  intValue\n}\nfragment secondInterfaceImplFields on SecondInterfaceImplementation {\n  id\n  name\n  floatValue\n}"

@Generated
public class InterfaceWithNamedFragmentsQuery :
    GraphQLClientRequest<InterfaceWithNamedFragmentsQuery.Result> {
  public override val query: String = INTERFACE_WITH_NAMED_FRAGMENTS_QUERY

  public override val operationName: String = "InterfaceWithNamedFragmentsQuery"

  public override fun responseType(): KClass<InterfaceWithNamedFragmentsQuery.Result> =
      InterfaceWithNamedFragmentsQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning an interface
     */
    public val interfaceQuery: BasicInterface,
  )
}
