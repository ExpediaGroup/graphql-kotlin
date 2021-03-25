package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.mixedcasequery.ScalarWrapper
import kotlin.String
import kotlin.reflect.KClass

public const val MI_XE_DCA_SE_QUERY: String =
    "query miXEDcaSEQuery {\n  scalarQuery {\n    name\n  }\n}"

public class MiXEDcaSEQuery : GraphQLClientRequest<MiXEDcaSEQuery.Result> {
  public override val query: String = MI_XE_DCA_SE_QUERY

  public override val operationName: String = "miXEDcaSEQuery"

  public override fun responseType(): KClass<MiXEDcaSEQuery.Result> = MiXEDcaSEQuery.Result::class

  public data class Result(
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    public val scalarQuery: ScalarWrapper
  )
}
