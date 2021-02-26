package com.expediagroup.graphql.generated.scalaraliasquery

import com.expediagroup.graphql.generated.ID
import com.expediagroup.graphql.generated.UUID
import kotlinx.serialization.Serializable

/**
 * Wrapper that holds all supported scalar types
 */
@Serializable
data class ScalarWrapper(
  /**
   * ID represents unique identifier that is not intended to be human readable
   */
  val id: ID,
  /**
   * Custom scalar
   */
  val custom: UUID
)
