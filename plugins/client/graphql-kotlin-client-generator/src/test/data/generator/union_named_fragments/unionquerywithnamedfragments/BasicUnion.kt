package com.expediagroup.graphql.generated.unionquerywithnamedfragments

import kotlin.Int
import kotlin.String
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
  val id: Int,
  /**
   * Object name
   */
  val name: String
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
  val id: Int,
  /**
   * Some object name
   */
  val name: String,
  /**
   * Optional value
   * Second line of the description
   */
  val optional: String?
) : BasicUnion()
