package com.expediagroup.graphql.generated.unionquerywithinlinefragments

import com.expediagroup.graphql.client.Generated
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Very basic union of BasicObject and ComplexObject
 */
@Generated
@Serializable
public sealed class BasicUnion

/**
 * Some basic description
 */
@Generated
@Serializable
@SerialName(value = "BasicObject")
public data class BasicObject(
  public val id: Int,
  /**
   * Object name
   */
  public val name: String,
) : BasicUnion()

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
@Generated
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
  public val optional: String?,
) : BasicUnion()

/**
 * Fallback BasicUnion implementation that will be used when unknown/unhandled type is encountered.
 *
 * NOTE: This fallback logic has to be manually registered with the instance of
 * GraphQLClientKotlinxSerializer. See documentation for details.
 */
@Generated
@Serializable
public class DefaultBasicUnionImplementation() : BasicUnion()
