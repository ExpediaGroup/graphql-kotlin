package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.reusedlisttypesquery.BasicObject
import com.expediagroup.graphql.generated.reusedlisttypesquery.BasicObject2
import com.expediagroup.graphql.generated.reusedlisttypesquery.ComplexObject
import com.expediagroup.graphql.generated.reusedlisttypesquery.ComplexObject2
import com.expediagroup.graphql.generated.reusedlisttypesquery.ComplexObject3
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

const val REUSED_LIST_TYPES_QUERY: String =
    "query ReusedListTypesQuery {\n  first: listQuery {\n    id\n    name\n  }\n  second: listQuery {\n    name\n  }\n  third: listQuery {\n    id\n    name\n  }\n  firstComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      id\n      name\n    }\n  }\n  secondComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      id\n      name\n    }\n  }\n  thirdComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      name\n    }\n  }\n  fourthComplex: complexObjectQuery {\n    id\n    basicList {\n      id\n    }\n  }\n}"

@Serializable
class ReusedListTypesQuery : GraphQLClientRequest<ReusedListTypesQuery.Result> {
  override val query: String = REUSED_LIST_TYPES_QUERY

  override val operationName: String = "ReusedListTypesQuery"

  override fun responseType(): KClass<ReusedListTypesQuery.Result> =
      ReusedListTypesQuery.Result::class

  @Serializable
  data class Result(
    /**
     * Query returning list of simple objects
     */
    val first: List<BasicObject>,
    /**
     * Query returning list of simple objects
     */
    val second: List<BasicObject2>,
    /**
     * Query returning list of simple objects
     */
    val third: List<BasicObject>,
    /**
     * Query returning an object that references another object
     */
    val firstComplex: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    val secondComplex: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    val thirdComplex: ComplexObject2,
    /**
     * Query returning an object that references another object
     */
    val fourthComplex: ComplexObject3
  )
}
