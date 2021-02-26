package com.expediagroup.graphql.generated.enums

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import kotlin.Deprecated

/**
 * Custom enum description
 */
enum class CustomEnum {
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
   * This is a default enum value that will be used when attempting to deserialize unknown value.
   */
  @JsonEnumDefaultValue
  __UNKNOWN_VALUE
}
