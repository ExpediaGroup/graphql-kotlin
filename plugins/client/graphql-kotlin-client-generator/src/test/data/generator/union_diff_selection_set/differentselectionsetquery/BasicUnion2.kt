package com.expediagroup.graphql.generated.differentselectionsetquery

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Very basic union of BasicObject and ComplexObject
 */
@Serializable
sealed class BasicUnion2

/**
 * Some basic description
 */
@Serializable
@SerialName(value = "BasicObject")
data class BasicObject2(
  /**
   * Object name
   */
  val name: String
) : BasicUnion2()

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
@Serializable
@SerialName(value = "ComplexObject")
data class ComplexObject2(
  /**
   * Some object name
   */
  val name: String
) : BasicUnion2()
