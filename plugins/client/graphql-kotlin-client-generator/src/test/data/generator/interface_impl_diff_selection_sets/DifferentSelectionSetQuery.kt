package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.differentselectionsetquery.BasicInterface
import com.expediagroup.graphql.generated.differentselectionsetquery.BasicInterface2
import kotlin.String
import kotlin.reflect.KClass

const val DIFFERENT_SELECTION_SET_QUERY: String =
    "query DifferentSelectionSetQuery {\n  first: interfaceQuery {\n    __typename\n    id\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n  second: interfaceQuery {\n    __typename\n    id\n    ... on FirstInterfaceImplementation {\n      name\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      name\n      floatValue\n    }\n  }\n}"

class DifferentSelectionSetQuery : GraphQLClientRequest<DifferentSelectionSetQuery.Result> {
  override val query: String = DIFFERENT_SELECTION_SET_QUERY

  override val operationName: String = "DifferentSelectionSetQuery"

  override fun responseType(): KClass<DifferentSelectionSetQuery.Result> =
      DifferentSelectionSetQuery.Result::class

  data class Result(
    /**
     * Query returning an interface
     */
    val first: BasicInterface,
    /**
     * Query returning an interface
     */
    val second: BasicInterface2
  )
}
