package com.expediagroup.graphql.generated

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.generated.inputs.ScalarWrapperInput
import com.expediagroup.graphql.generated.scalars.AnyToULocaleConverter
import com.expediagroup.graphql.generated.scalars.OptionalScalarInputSerializer
import com.expediagroup.graphql.generated.scalars.ULocaleToAnyConverter
import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.databind.`annotation`.JsonDeserialize
import com.fasterxml.jackson.databind.`annotation`.JsonSerialize
import com.ibm.icu.util.ULocale
import kotlin.Boolean
import kotlin.String
import kotlin.reflect.KClass

public const val CUSTOM_SCALAR_INPUT_QUERY: String =
    "query CustomScalarInputQuery(${'$'}requiredLocale: Locale!, ${'$'}optionalLocale: Locale, ${'$'}scalarWrapper: ScalarWrapperInput) {\n  inputCustomScalarQuery(requiredLocale: ${'$'}requiredLocale, optionalLocale: ${'$'}optionalLocale, scalarWrapper: ${'$'}scalarWrapper)\n}"

@Generated
public class CustomScalarInputQuery(
  public override val variables: CustomScalarInputQuery.Variables,
) : GraphQLClientRequest<CustomScalarInputQuery.Result> {
  public override val query: String = CUSTOM_SCALAR_INPUT_QUERY

  public override val operationName: String = "CustomScalarInputQuery"

  public override fun responseType(): KClass<CustomScalarInputQuery.Result> =
      CustomScalarInputQuery.Result::class

  @Generated
  public data class Variables(
    @JsonSerialize(converter = ULocaleToAnyConverter::class)
    @JsonDeserialize(converter = AnyToULocaleConverter::class)
    @get:JsonProperty(value = "requiredLocale")
    public val requiredLocale: ULocale,
    @JsonSerialize(using = OptionalScalarInputSerializer::class)
    @get:JsonProperty(value = "optionalLocale")
    public val optionalLocale: OptionalInput<ULocale> = OptionalInput.Undefined,
    @get:JsonProperty(value = "scalarWrapper")
    public val scalarWrapper: OptionalInput<ScalarWrapperInput> = OptionalInput.Undefined,
  )

  @Generated
  public data class Result(
    /**
     * Query that accepts a custom scalar input
     */
    public val inputCustomScalarQuery: Boolean,
  )
}
