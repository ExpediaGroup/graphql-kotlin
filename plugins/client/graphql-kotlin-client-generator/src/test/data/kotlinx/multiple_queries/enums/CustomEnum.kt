package com.expediagroup.graphql.generated.enums

import com.expediagroup.graphql.client.Generated
import kotlin.Deprecated
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Custom enum description
 */
@Generated
@Serializable
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
  @SerialName("four")
  FOUR,
  /**
   * This is a default enum value that will be used when attempting to deserialize unknown value.
   */
  __UNKNOWN_VALUE,
}
