package com.expediagroup.graphql.generated.customscalarquery

import com.expediagroup.graphql.generated.scalars.AnyToUUIDConverter
import com.expediagroup.graphql.generated.scalars.UUIDToStringConverter
import com.fasterxml.jackson.databind.`annotation`.JsonDeserialize
import com.fasterxml.jackson.databind.`annotation`.JsonSerialize
import java.util.UUID

/**
 * Wrapper that holds all supported scalar types
 */
public data class ScalarWrapper(
  /**
   * Custom scalar
   */
  @JsonSerialize(converter = UUIDToStringConverter::class)
  @JsonDeserialize(converter = AnyToUUIDConverter::class)
  public val custom: UUID
)
