package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.reusedlisttypesquery.BasicObject
import com.expediagroup.graphql.generated.reusedlisttypesquery.BasicObject2
import com.expediagroup.graphql.generated.reusedlisttypesquery.ComplexObject
import com.expediagroup.graphql.generated.reusedlisttypesquery.ComplexObject2
import com.expediagroup.graphql.generated.reusedlisttypesquery.ComplexObject3
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val REUSED_LIST_TYPES_QUERY: String =
    "query ReusedListTypesQuery {\n  first: listQuery {\n    id\n    name\n  }\n  second: listQuery {\n    name\n  }\n  third: listQuery {\n    id\n    name\n  }\n  firstComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      id\n      name\n    }\n  }\n  secondComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      id\n      name\n    }\n  }\n  thirdComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      name\n    }\n  }\n  fourthComplex: complexObjectQuery {\n    id\n    basicList {\n      id\n    }\n  }\n}"

@Generated
public class ReusedListTypesQuery : GraphQLClientRequest<ReusedListTypesQuery.Result> {
  public override val query: String = REUSED_LIST_TYPES_QUERY

  public override val operationName: String = "ReusedListTypesQuery"

  public override fun responseType(): KClass<ReusedListTypesQuery.Result> =
      ReusedListTypesQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning list of simple objects
     */
    public val first: List<BasicObject>,
    /**
     * Query returning list of simple objects
     */
    public val second: List<BasicObject2>,
    /**
     * Query returning list of simple objects
     */
    public val third: List<BasicObject>,
    /**
     * Query returning an object that references another object
     */
    public val firstComplex: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    public val secondComplex: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    public val thirdComplex: ComplexObject2,
    /**
     * Query returning an object that references another object
     */
    public val fourthComplex: ComplexObject3,
  )
}
