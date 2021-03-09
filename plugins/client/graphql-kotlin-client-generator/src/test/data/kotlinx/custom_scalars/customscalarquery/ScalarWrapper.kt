package com.expediagroup.graphql.generated.customscalarquery

import com.expediagroup.graphql.generated.ID
import com.expediagroup.graphql.generated.scalars.UUID
import kotlin.Int
import kotlinx.serialization.Serializable

/**
 * Wrapper that holds all supported scalar types
 */
@Serializable
data class ScalarWrapper(
  /**
   * A signed 32-bit nullable integer value
   */
  val count: Int?,
  /**
   * Custom scalar
   */
  val custom: UUID,
  /**
   * ID represents unique identifier that is not intended to be human readable
   */
  val id: ID
)
