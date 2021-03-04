package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.objectwithnamedfragmentquery.ComplexObject
import kotlin.String
import kotlin.reflect.KClass

const val OBJECT_WITH_NAMED_FRAGMENT_QUERY: String =
    "query ObjectWithNamedFragmentQuery {\n  complexObjectQuery {\n    ...complexObjectFields\n  }\n}\n\nfragment complexObjectFields on ComplexObject {\n  id\n  name\n  details {\n    ...detailObjectFields\n  }\n}\n\nfragment detailObjectFields on DetailsObject {\n  value\n}"

class ObjectWithNamedFragmentQuery : GraphQLClientRequest<ObjectWithNamedFragmentQuery.Result> {
  override val query: String = OBJECT_WITH_NAMED_FRAGMENT_QUERY

  override val operationName: String = "ObjectWithNamedFragmentQuery"

  override fun responseType(): KClass<ObjectWithNamedFragmentQuery.Result> =
      ObjectWithNamedFragmentQuery.Result::class

  data class Result(
    /**
     * Query returning an object that references another object
     */
    val complexObjectQuery: ComplexObject
  )
}
