package com.expediagroup.graphql.generated.differentselectionsetquery

import kotlin.Int
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Very basic union of BasicObject and ComplexObject
 */
@Serializable
sealed class BasicUnion

/**
 * Some basic description
 */
@Serializable
@SerialName(value = "BasicObject")
data class BasicObject(
  val id: Int
) : BasicUnion()

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
@Serializable
@SerialName(value = "ComplexObject")
data class ComplexObject(
  /**
   * Some unique identifier
   */
  val id: Int
) : BasicUnion()
