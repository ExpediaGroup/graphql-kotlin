package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.ScalarWrapperInput
import com.expediagroup.graphql.generated.scalars.OptionalScalarWrapperInputSerializer
import com.expediagroup.graphql.generated.scalars.OptionalULocaleSerializer
import com.expediagroup.graphql.generated.scalars.ULocaleSerializer
import com.ibm.icu.util.ULocale
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

public const val CUSTOM_SCALAR_INPUT_QUERY: String =
    "query CustomScalarInputQuery(${'$'}requiredLocale: Locale!, ${'$'}optionalLocale: Locale, ${'$'}scalarWrapper: ScalarWrapperInput) {\n  inputCustomScalarQuery(requiredLocale: ${'$'}requiredLocale, optionalLocale: ${'$'}optionalLocale, scalarWrapper: ${'$'}scalarWrapper)\n}"

@Generated
@Serializable
public class CustomScalarInputQuery(
  public override val variables: CustomScalarInputQuery.Variables,
) : GraphQLClientRequest<CustomScalarInputQuery.Result> {
  @Required
  public override val query: String = CUSTOM_SCALAR_INPUT_QUERY

  @Required
  public override val operationName: String = "CustomScalarInputQuery"

  public override fun responseType(): KClass<CustomScalarInputQuery.Result> =
      CustomScalarInputQuery.Result::class

  @Generated
  @Serializable
  public data class Variables(
    @Serializable(with = ULocaleSerializer::class)
    public val requiredLocale: ULocale,
    @Serializable(with = OptionalULocaleSerializer::class)
    public val optionalLocale: OptionalInput<@Serializable(with = ULocaleSerializer::class) ULocale>
        = OptionalInput.Undefined,
    @Serializable(with = OptionalScalarWrapperInputSerializer::class)
    public val scalarWrapper: OptionalInput<ScalarWrapperInput> = OptionalInput.Undefined,
  )

  @Generated
  @Serializable
  public data class Result(
    /**
     * Query that accepts a custom scalar input
     */
    public val inputCustomScalarQuery: Boolean,
  )
}
