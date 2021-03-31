package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.enums.CustomEnum
import com.expediagroup.graphql.generated.enums.CustomEnum.__UNKNOWN_VALUE
import com.expediagroup.graphql.generated.firstquery.BasicInterface
import com.expediagroup.graphql.generated.firstquery.ComplexObject
import com.expediagroup.graphql.generated.firstquery.ScalarWrapper
import com.expediagroup.graphql.generated.inputs.ComplexArgumentInput
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

public const val FIRST_QUERY: String =
    "query FirstQuery(${'$'}input: ComplexArgumentInput) {\n  complexInputObjectQuery(criteria: ${'$'}input)\n  complexObjectQuery {\n    id\n    name\n    optional\n    details {\n      id\n      flag\n      value\n    }\n  }\n  enumQuery\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n  scalarQuery {\n    count\n    custom\n    id\n  }\n}"

@Serializable
public class FirstQuery(
  public override val variables: FirstQuery.Variables
) : GraphQLClientRequest<FirstQuery.Result> {
  public override val query: String = FIRST_QUERY

  public override val operationName: String = "FirstQuery"

  public override fun responseType(): KClass<FirstQuery.Result> = FirstQuery.Result::class

  @Serializable
  public data class Variables(
    public val input: ComplexArgumentInput? = null
  )

  @Serializable
  public data class Result(
    /**
     * Query that accepts self referencing input object
     */
    public val complexInputObjectQuery: Boolean,
    /**
     * Query returning an object that references another object
     */
    public val complexObjectQuery: ComplexObject,
    /**
     * Query that returns enum value
     */
    public val enumQuery: CustomEnum = CustomEnum.__UNKNOWN_VALUE,
    /**
     * Query returning an interface
     */
    public val interfaceQuery: BasicInterface,
    /**
     * Query that returns wrapper object with all supported scalar types
     */
    public val scalarQuery: ScalarWrapper
  )
}
