package com.expediagroup.graphql.generated.differentselectionsetquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.Id.NAME
import kotlin.String

/**
 * Very basic union of BasicObject and ComplexObject
 */
@Generated
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "__typename",
  defaultImpl = DefaultBasicUnion2Implementation::class,
)
@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    BasicObject2::class,
    name="BasicObject"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    ComplexObject2::class, name="ComplexObject")])
public interface BasicUnion2

/**
 * Some basic description
 */
@Generated
public data class BasicObject2(
  /**
   * Object name
   */
  public val name: String,
) : BasicUnion2

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
@Generated
public data class ComplexObject2(
  /**
   * Some object name
   */
  public val name: String,
) : BasicUnion2

/**
 * Fallback BasicUnion2 implementation that will be used when unknown/unhandled type is encountered.
 */
@Generated
public class DefaultBasicUnion2Implementation() : BasicUnion2
