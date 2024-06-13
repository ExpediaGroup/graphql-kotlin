package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.reusedlisttypesquery.BasicObject
import com.expediagroup.graphql.generated.reusedlisttypesquery.BasicObject2
import com.expediagroup.graphql.generated.reusedlisttypesquery.ComplexObject
import com.expediagroup.graphql.generated.reusedlisttypesquery.ComplexObject2
import com.expediagroup.graphql.generated.reusedlisttypesquery.ComplexObject3
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val REUSED_LIST_TYPES_QUERY: String =
    "query ReusedListTypesQuery {\n  first: listQuery {\n    id\n    name\n  }\n  second: listQuery {\n    name\n  }\n  third: listQuery {\n    id\n    name\n  }\n  firstComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      id\n      name\n    }\n  }\n  secondComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      id\n      name\n    }\n  }\n  thirdComplex: complexObjectQuery {\n    id\n    name\n    basicList {\n      name\n    }\n  }\n  fourthComplex: complexObjectQuery {\n    id\n    basicList {\n      id\n    }\n  }\n}"

@Generated
public class ReusedListTypesQuery : GraphQLClientRequest<ReusedListTypesQuery.Result> {
  override val query: String = REUSED_LIST_TYPES_QUERY

  override val operationName: String = "ReusedListTypesQuery"

  override fun responseType(): KClass<ReusedListTypesQuery.Result> =
      ReusedListTypesQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning list of simple objects
     */
    @get:JsonProperty(value = "first")
    public val first: List<BasicObject>,
    /**
     * Query returning list of simple objects
     */
    @get:JsonProperty(value = "second")
    public val second: List<BasicObject2>,
    /**
     * Query returning list of simple objects
     */
    @get:JsonProperty(value = "third")
    public val third: List<BasicObject>,
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "firstComplex")
    public val firstComplex: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "secondComplex")
    public val secondComplex: ComplexObject,
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "thirdComplex")
    public val thirdComplex: ComplexObject2,
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "fourthComplex")
    public val fourthComplex: ComplexObject3,
  )
}
