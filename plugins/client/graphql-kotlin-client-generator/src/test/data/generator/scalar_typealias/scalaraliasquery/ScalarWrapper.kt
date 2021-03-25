package com.expediagroup.graphql.generated.scalaraliasquery

import com.expediagroup.graphql.generated.ID
import com.expediagroup.graphql.generated.UUID

/**
 * Wrapper that holds all supported scalar types
 */
public data class ScalarWrapper(
  /**
   * ID represents unique identifier that is not intended to be human readable
   */
  public val id: ID,
  /**
   * Custom scalar
   */
  public val custom: UUID
)
