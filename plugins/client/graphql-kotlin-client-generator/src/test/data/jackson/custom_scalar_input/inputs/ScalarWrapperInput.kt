package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.generated.ID
import com.expediagroup.graphql.generated.scalars.AnyToULocaleConverter
import com.expediagroup.graphql.generated.scalars.AnyToUUIDConverter
import com.expediagroup.graphql.generated.scalars.ULocaleToAnyConverter
import com.expediagroup.graphql.generated.scalars.UUIDToAnyConverter
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
  public val count: OptionalInput<Int> = OptionalInput.Undefined,
  /**
   * Custom scalar of UUID
   * NOTE: This field was not wrapped in optional as currently custom scalars do not work with
   * optional wrappers.
   */
  @JsonSerialize(converter = UUIDToAnyConverter::class)
  @JsonDeserialize(converter = AnyToUUIDConverter::class)
  public val custom: UUID? = null,
  /**
   * List of custom scalar UUIDs
   * NOTE: This field was not wrapped in optional as currently custom scalars do not work with
   * optional wrappers.
   */
  @JsonSerialize(contentConverter = UUIDToAnyConverter::class)
  @JsonDeserialize(contentConverter = AnyToUUIDConverter::class)
  public val customList: List<UUID>? = null,
  /**
   * ID represents unique identifier that is not intended to be human readable
   */
  public val id: ID,
  /**
   * UTF-8 character sequence
   */
  public val name: String,
  /**
   * A nullable signed double-precision floating-point value
   */
  public val rating: OptionalInput<Double> = OptionalInput.Undefined,
  /**
   * Either true or false
   */
  public val valid: Boolean,
  /**
   * Custom scalar of Locale
   */
  @JsonSerialize(converter = ULocaleToAnyConverter::class)
  @JsonDeserialize(converter = AnyToULocaleConverter::class)
  public val locale: ULocale,
  /**
   * List of custom scalar Locales
   */
  @JsonSerialize(contentConverter = ULocaleToAnyConverter::class)
  @JsonDeserialize(contentConverter = AnyToULocaleConverter::class)
  public val listLocale: List<ULocale>
)
