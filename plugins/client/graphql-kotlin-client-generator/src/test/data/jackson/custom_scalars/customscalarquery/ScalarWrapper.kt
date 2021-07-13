package com.expediagroup.graphql.generated.customscalarquery

import com.expediagroup.graphql.generated.scalars.AnyToUUIDConverter
import com.expediagroup.graphql.generated.scalars.UUIDToAnyConverter
import com.fasterxml.jackson.databind.`annotation`.JsonDeserialize
import com.fasterxml.jackson.databind.`annotation`.JsonSerialize
import java.util.UUID
import kotlin.collections.List

/**
 * Wrapper that holds all supported scalar types
 */
public data class ScalarWrapper(
  /**
   * Custom scalar
   */
  @JsonSerialize(converter = UUIDToAnyConverter::class)
  @JsonDeserialize(converter = AnyToUUIDConverter::class)
  public val custom: UUID,
  /**
   * List of custom scalars
   */
  @JsonSerialize(contentConverter = UUIDToAnyConverter::class)
  @JsonDeserialize(contentConverter = AnyToUUIDConverter::class)
  public val customList: List<UUID>
)
