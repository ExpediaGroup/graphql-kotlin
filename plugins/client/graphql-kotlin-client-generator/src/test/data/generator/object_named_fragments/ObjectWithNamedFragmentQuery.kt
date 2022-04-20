package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.objectwithnamedfragmentquery.ComplexObject
import kotlin.String
import kotlin.reflect.KClass

public const val OBJECT_WITH_NAMED_FRAGMENT_QUERY: String =
    "query ObjectWithNamedFragmentQuery {\n  complexObjectQuery {\n    ...complexObjectFields\n  }\n}\n\nfragment complexObjectFields on ComplexObject {\n  id\n  name\n  details {\n    ...detailObjectFields\n  }\n}\n\nfragment detailObjectFields on DetailsObject {\n  value\n}"

@Generated
public class ObjectWithNamedFragmentQuery :
    GraphQLClientRequest<ObjectWithNamedFragmentQuery.Result> {
  public override val query: String = OBJECT_WITH_NAMED_FRAGMENT_QUERY

  public override val operationName: String = "ObjectWithNamedFragmentQuery"

  public override fun responseType(): KClass<ObjectWithNamedFragmentQuery.Result> =
      ObjectWithNamedFragmentQuery.Result::class

  @Generated
  public data class Result(
    /**
     * Query returning an object that references another object
     */
    public val complexObjectQuery: ComplexObject,
  )
}
