package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.interfacemissingtypeselection.BasicInterface
import kotlin.String
import kotlin.reflect.KClass

public const val INTERFACE_MISSING_TYPE_SELECTION: String =
    "query InterfaceMissingTypeSelection {\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n  }\n}"

@Generated
public class InterfaceMissingTypeSelection :
    GraphQLClientRequest<InterfaceMissingTypeSelection.Result> {
  public override val query: String = INTERFACE_MISSING_TYPE_SELECTION

  public override val operationName: String = "InterfaceMissingTypeSelection"

  public override fun responseType(): KClass<InterfaceMissingTypeSelection.Result> =
      InterfaceMissingTypeSelection.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning an interface
     */
    public val interfaceQuery: BasicInterface,
  )
}
