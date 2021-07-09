package com.expediagroup.graphql.generated.customscalarquery

import com.expediagroup.graphql.generated.BigInteger
import com.expediagroup.graphql.generated.scalars.UUID

/**
 * Wrapper that holds all supported scalar types
 */
public data class ScalarWrapper(
  /**
   * Custom scalar
   */
  public val custom: UUID,
  /**
   * Custom BigInteger scalar
   */
  public val bigInteger: BigInteger?
)
