package com.expediagroup.graphql.generated.firstquery

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.generated.ID
import com.expediagroup.graphql.generated.scalars.UUIDSerializer
import java.util.UUID
import kotlin.Int
import kotlinx.serialization.Serializable

/**
 * Wrapper that holds all supported scalar types
 */
@Generated
@Serializable
public data class ScalarWrapper(
  /**
   * A signed 32-bit nullable integer value
   */
  public val count: Int? = null,
  /**
   * Custom scalar of UUID
   */
  @Serializable(with = UUIDSerializer::class)
  public val custom: UUID? = null,
  /**
   * ID represents unique identifier that is not intended to be human readable
   */
  public val id: ID,
)
