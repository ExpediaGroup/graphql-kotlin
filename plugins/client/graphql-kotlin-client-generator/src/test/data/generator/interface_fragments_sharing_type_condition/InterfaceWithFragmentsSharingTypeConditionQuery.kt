package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.interfacewithfragmentssharingtypeconditionquery.BasicInterface
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.reflect.KClass

public const val INTERFACE_WITH_FRAGMENTS_SHARING_TYPE_CONDITION_QUERY: String =
    "query InterfaceWithFragmentsSharingTypeConditionQuery {\n  interfaceQuery {\n    __typename\n    ...interfaceIdFields\n    ...interfaceNameFields\n    ...firstImplementationIdFields\n    ...firstImplementationValueFields\n    ...secondImplementationFields\n  }\n}\n\nfragment interfaceIdFields on BasicInterface {\n  id\n}\n\nfragment interfaceNameFields on BasicInterface {\n  name\n}\n\nfragment firstImplementationIdFields on FirstInterfaceImplementation {\n  id\n}\n\nfragment firstImplementationValueFields on FirstInterfaceImplementation {\n  intValue\n}\n\nfragment secondImplementationFields on SecondInterfaceImplementation {\n  floatValue\n}"

@Generated
public class InterfaceWithFragmentsSharingTypeConditionQuery :
    GraphQLClientRequest<InterfaceWithFragmentsSharingTypeConditionQuery.Result> {
  override val query: String = INTERFACE_WITH_FRAGMENTS_SHARING_TYPE_CONDITION_QUERY

  override val operationName: String = "InterfaceWithFragmentsSharingTypeConditionQuery"

  override fun responseType(): KClass<InterfaceWithFragmentsSharingTypeConditionQuery.Result> =
      InterfaceWithFragmentsSharingTypeConditionQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning an interface
     */
    @get:JsonProperty(value = "interfaceQuery")
    public val interfaceQuery: BasicInterface,
  )
}
