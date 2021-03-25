package com.expediagroup.graphql.generated.differentselectionsetquery

import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.Id.NAME
import kotlin.Int

/**
 * Very basic union of BasicObject and ComplexObject
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "__typename"
)
@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    BasicObject::class, name="BasicObject"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value
    = ComplexObject::class, name="ComplexObject")])
public interface BasicUnion

/**
 * Some basic description
 */
public data class BasicObject(
  public val id: Int
) : BasicUnion

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
public data class ComplexObject(
  /**
   * Some unique identifier
   */
  public val id: Int
) : BasicUnion
