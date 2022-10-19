package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.generated.ID
import com.expediagroup.graphql.generated.scalars.AnyToULocaleConverter
import com.expediagroup.graphql.generated.scalars.OptionalScalarInputSerializer
import com.expediagroup.graphql.generated.scalars.ULocaleToAnyConverter
import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.databind.`annotation`.JsonDeserialize
import com.fasterxml.jackson.databind.`annotation`.JsonSerialize
import com.ibm.icu.util.ULocale
import java.util.UUID
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Wrapper that holds all supported scalar types
 */
@Generated
public data class ScalarWrapperInput(
  /**
   * A signed 32-bit nullable integer value
   */
  @get:JsonProperty(value = "count")
  public val count: OptionalInput<Int> = OptionalInput.Undefined,
  /**
   * Custom scalar of UUID
   */
  @JsonSerialize(using = OptionalScalarInputSerializer::class)
  @get:JsonProperty(value = "custom")
  public val custom: OptionalInput<UUID> = OptionalInput.Undefined,
  /**
   * List of custom scalar UUIDs
   */
  @JsonSerialize(using = OptionalScalarInputSerializer::class)
  @get:JsonProperty(value = "customList")
  public val customList: OptionalInput<List<UUID>> = OptionalInput.Undefined,
  /**
   * ID represents unique identifier that is not intended to be human readable
   */
  @get:JsonProperty(value = "id")
  public val id: ID,
  /**
   * Optional ID
   */
  @get:JsonProperty(value = "optionalId")
  public val optionalId: OptionalInput<ID> = OptionalInput.Undefined,
  /**
   * UTF-8 character sequence
   */
  @get:JsonProperty(value = "name")
  public val name: String,
  /**
   * Optional list of names
   */
  @get:JsonProperty(value = "nameList")
  public val nameList: OptionalInput<List<String>> = OptionalInput.Undefined,
  /**
   * A nullable signed double-precision floating-point value
   */
  @get:JsonProperty(value = "rating")
  public val rating: OptionalInput<Double> = OptionalInput.Undefined,
  /**
   * Either true or false
   */
  @get:JsonProperty(value = "valid")
  public val valid: Boolean,
  /**
   * Custom scalar of Locale
   */
  @JsonSerialize(converter = ULocaleToAnyConverter::class)
  @JsonDeserialize(converter = AnyToULocaleConverter::class)
  @get:JsonProperty(value = "locale")
  public val locale: ULocale,
  /**
   * List of custom scalar Locales
   */
  @JsonSerialize(contentConverter = ULocaleToAnyConverter::class)
  @JsonDeserialize(contentConverter = AnyToULocaleConverter::class)
  @get:JsonProperty(value = "listLocale")
  public val listLocale: List<ULocale>,
)
