package com.expediagroup.graphql.generated.enums

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
  __UNKNOWN_VALUE
}
