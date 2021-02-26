package com.expediagroup.graphql.generated.documentationquery

import kotlin.Int
import kotlinx.serialization.Serializable

/**
 * Doc object with % and $ floating around
 */
@Serializable
data class DocObject(
  /**
   * An id with a comment containing % and $ as well
   */
  val id: Int
)
