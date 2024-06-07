package com.expediagroup.graphql.generated.scalaraliasquery

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.generated.ID
import com.expediagroup.graphql.generated.UUID
import com.fasterxml.jackson.`annotation`.JsonProperty

/**
 * Wrapper that holds all supported scalar types
 */
@Generated
public data class ScalarWrapper(
  /**
   * ID represents unique identifier that is not intended to be human readable
   */
  @get:JsonProperty(value = "id")
  public val id: ID,
  /**
   * Custom scalar of UUID
   */
  @get:JsonProperty(value = "custom")
  public val custom: UUID? = null,
)
