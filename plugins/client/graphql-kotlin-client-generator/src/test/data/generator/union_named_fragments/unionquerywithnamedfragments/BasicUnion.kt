package com.expediagroup.graphql.generated.unionquerywithnamedfragments

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.Id.NAME
import kotlin.Int
import kotlin.String

/**
 * Very basic union of BasicObject and ComplexObject
 */
@Generated
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "__typename",
  defaultImpl = DefaultBasicUnionImplementation::class,
)
@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    BasicObject::class, name="BasicObject"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value
    = ComplexObject::class, name="ComplexObject")])
public interface BasicUnion

/**
 * Some basic description
 */
@Generated
public data class BasicObject(
  public val id: Int,
  /**
   * Object name
   */
  public val name: String,
) : BasicUnion

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
   * Optional value
   * Second line of the description
   */
  public val optional: String?,
) : BasicUnion

/**
 * Fallback BasicUnion implementation that will be used when unknown/unhandled type is encountered.
 */
@Generated
public class DefaultBasicUnionImplementation() : BasicUnion
