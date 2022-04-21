package com.expediagroup.graphql.generated.customscalarquery

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.generated.scalars.AnyToULocaleConverter
import com.expediagroup.graphql.generated.scalars.AnyToUUIDConverter
import com.expediagroup.graphql.generated.scalars.ULocaleToAnyConverter
import com.expediagroup.graphql.generated.scalars.UUIDToAnyConverter
import com.fasterxml.jackson.databind.`annotation`.JsonDeserialize
import com.fasterxml.jackson.databind.`annotation`.JsonSerialize
import com.ibm.icu.util.ULocale
import java.util.UUID
import kotlin.collections.List

/**
 * Wrapper that holds all supported scalar types
 */
@Generated
public data class ScalarWrapper(
  /**
   * Custom scalar of UUID
   */
  @JsonSerialize(converter = UUIDToAnyConverter::class)
  @JsonDeserialize(converter = AnyToUUIDConverter::class)
  public val custom: UUID? = null,
  /**
   * List of custom scalar UUIDs
   */
  @JsonSerialize(contentConverter = UUIDToAnyConverter::class)
  @JsonDeserialize(contentConverter = AnyToUUIDConverter::class)
  public val customList: List<UUID>? = null,
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
  public val listLocale: List<ULocale>,
)
