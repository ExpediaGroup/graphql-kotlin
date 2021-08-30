package com.expediagroup.graphql.generated.enums

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonEnumDefaultValue
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Deprecated

/**
 * Custom enum description
 */
@Generated
public enum class CustomEnum {
  /**
   * First enum value
   */
  ONE,
  /**
   * Third enum value
   */
  @Deprecated(message = "only goes up to two")
  THREE,
  /**
   * Second enum value
   */
  TWO,
  /**
   * Lowercase enum value
   */
  @JsonProperty("four")
  FOUR,
  /**
   * This is a default enum value that will be used when attempting to deserialize unknown value.
   */
  @JsonEnumDefaultValue
  __UNKNOWN_VALUE,
}
