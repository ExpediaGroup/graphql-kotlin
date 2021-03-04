package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.mixedcasequery.ScalarWrapper
import kotlin.String
import kotlin.reflect.KClass

const val MI_XE_DCA_SE_QUERY: String = "query miXEDcaSEQuery {\n  scalarQuery {\n    name\n  }\n}"

class MiXEDcaSEQuery : GraphQLClientRequest<MiXEDcaSEQuery.Result> {
  override val query: String = MI_XE_DCA_SE_QUERY

  override val operationName: String = "miXEDcaSEQuery"

  override fun responseType(): KClass<MiXEDcaSEQuery.Result> = MiXEDcaSEQuery.Result::class

  data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    val scalarQuery: ScalarWrapper
  )
}
