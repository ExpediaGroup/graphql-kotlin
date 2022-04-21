package com.expediagroup.graphql.generated.reusedlisttypesquery

import com.expediagroup.graphql.client.Generated
import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
@Generated
public data class ComplexObject(
  /**
   * Some unique identifier
   */
  public val id: Int,
  /**
   * Some object name
   */
  public val name: String,
  /**
   * List of objects
   */
  public val basicList: List<BasicObject>,
)
