package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.differentselectionsetquery.BasicInterface
import com.expediagroup.graphql.generated.differentselectionsetquery.BasicInterface2
import kotlin.String
import kotlin.reflect.KClass

public const val DIFFERENT_SELECTION_SET_QUERY: String =
    "query DifferentSelectionSetQuery {\n  first: interfaceQuery {\n    __typename\n    id\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n  second: interfaceQuery {\n    __typename\n    id\n    ... on FirstInterfaceImplementation {\n      name\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      name\n      floatValue\n    }\n  }\n}"

@Generated
public class DifferentSelectionSetQuery : GraphQLClientRequest<DifferentSelectionSetQuery.Result> {
  public override val query: String = DIFFERENT_SELECTION_SET_QUERY

  public override val operationName: String = "DifferentSelectionSetQuery"

  public override fun responseType(): KClass<DifferentSelectionSetQuery.Result> =
      DifferentSelectionSetQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning an interface
     */
    public val first: BasicInterface,
    /**
     * Query returning an interface
     */
    public val second: BasicInterface2,
  )
}
