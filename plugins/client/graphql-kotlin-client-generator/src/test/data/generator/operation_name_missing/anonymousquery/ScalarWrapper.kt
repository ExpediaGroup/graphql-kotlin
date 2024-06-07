package com.expediagroup.graphql.generated.anonymousquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

/**
 * Wrapper that holds all supported scalar types
 */
@Generated
public data class ScalarWrapper(
  /**
   * UTF-8 character sequence
   */
  @get:JsonProperty(value = "name")
  public val name: String,
)
