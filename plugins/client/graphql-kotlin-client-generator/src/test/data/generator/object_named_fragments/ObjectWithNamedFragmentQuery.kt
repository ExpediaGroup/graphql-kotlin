package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.objectwithnamedfragmentquery.ComplexObject
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.reflect.KClass

public const val OBJECT_WITH_NAMED_FRAGMENT_QUERY: String =
    "query ObjectWithNamedFragmentQuery {\n  complexObjectQuery {\n    ...complexObjectFields\n  }\n}\n\nfragment complexObjectFields on ComplexObject {\n  id\n  name\n  details {\n    ...detailObjectFields\n  }\n}\n\nfragment detailObjectFields on DetailsObject {\n  value\n}"

@Generated
public class ObjectWithNamedFragmentQuery :
    GraphQLClientRequest<ObjectWithNamedFragmentQuery.Result> {
  override val query: String = OBJECT_WITH_NAMED_FRAGMENT_QUERY

  override val operationName: String = "ObjectWithNamedFragmentQuery"

  override fun responseType(): KClass<ObjectWithNamedFragmentQuery.Result> =
      ObjectWithNamedFragmentQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning an object that references another object
     */
    @get:JsonProperty(value = "complexObjectQuery")
    public val complexObjectQuery: ComplexObject,
  )
}
