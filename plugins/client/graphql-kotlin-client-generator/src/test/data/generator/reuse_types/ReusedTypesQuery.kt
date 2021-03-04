package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.reusedtypesquery.ComplexObject
import com.expediagroup.graphql.generated.reusedtypesquery.ComplexObject2
import com.expediagroup.graphql.generated.reusedtypesquery.ComplexObject3
import kotlin.String
import kotlin.reflect.KClass

const val REUSED_TYPES_QUERY: String =
    "query ReusedTypesQuery {\n  first: complexObjectQuery {\n    id\n    name\n  }\n  second: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n  third: complexObjectQuery {\n    id\n    name\n    details {\n      id\n    }\n  }\n  fourth: complexObjectQuery {\n    id\n    name\n  }\n  fifth: complexObjectQuery {\n    id\n    name\n    details {\n      id\n      value\n    }\n  }\n}"

class ReusedTypesQuery : GraphQLClientRequest<ReusedTypesQuery.Result> {
  override val query: String = REUSED_TYPES_QUERY

  override val operationName: String = "ReusedTypesQuery"

  override fun responseType(): KClass<ReusedTypesQuery.Result> = ReusedTypesQuery.Result::class

  data class Result(
    /**
     * Query returning an object that references another object
     */
    val first: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    val second: ComplexObject2,
    /**
     * Query returning an object that references another object
     */
    val third: ComplexObject3,
    /**
     * Query returning an object that references another object
     */
    val fourth: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    val fifth: ComplexObject2
  )
}
