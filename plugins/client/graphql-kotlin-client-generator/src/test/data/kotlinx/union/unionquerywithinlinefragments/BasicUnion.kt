package com.expediagroup.graphql.generated.unionquerywithinlinefragments

import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Very basic union of BasicObject and ComplexObject
 */
@Serializable
public sealed class BasicUnion

/**
 * Some basic description
 */
@Serializable
@SerialName(value = "BasicObject")
public data class BasicObject(
  public val id: Int,
  /**
   * Object name
   */
  public val name: String
) : BasicUnion()

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
@Serializable
@SerialName(value = "ComplexObject")
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
   * Optional value
   * Second line of the description
   */
  public val optional: String?
) : BasicUnion()
