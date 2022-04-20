package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.enums.CustomEnum
import com.expediagroup.graphql.generated.inputs.ComplexArgumentInput
import com.expediagroup.graphql.generated.scalars.OptionalComplexArgumentInputSerializer
import com.expediagroup.graphql.generated.secondquery.BasicInterface
import com.expediagroup.graphql.generated.secondquery.ComplexObject
import com.expediagroup.graphql.generated.secondquery.ScalarWrapper
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

public const val SECOND_QUERY: String =
    "query SecondQuery(${'$'}input: ComplexArgumentInput) {\n  complexInputObjectQuery(criteria: ${'$'}input)\n  complexObjectQuery {\n    id\n    name\n  }\n  enumQuery\n  interfaceQuery {\n    __typename\n    id\n    name\n    ... on FirstInterfaceImplementation {\n      intValue\n    }\n    ... on SecondInterfaceImplementation {\n      floatValue\n    }\n  }\n  scalarQuery {\n    count\n    custom\n    id\n  }\n}"

@Generated
@Serializable
public class SecondQuery(
  public override val variables: SecondQuery.Variables,
) : GraphQLClientRequest<SecondQuery.Result> {
  @Required
  public override val query: String = SECOND_QUERY

  @Required
  public override val operationName: String = "SecondQuery"

  public override fun responseType(): KClass<SecondQuery.Result> = SecondQuery.Result::class

  @Generated
  @Serializable
  public data class Variables(
    @Serializable(with = OptionalComplexArgumentInputSerializer::class)
    public val input: OptionalInput<ComplexArgumentInput> = OptionalInput.Undefined,
  )

  @Generated
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
    public val scalarQuery: ScalarWrapper,
  )
}
