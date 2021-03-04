package com.expediagroup.graphql.generated.differentselectionsetquery

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
import kotlin.String

/**
 * Very basic union of BasicObject and ComplexObject
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "__typename"
)
@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    BasicObject2::class,
    name="BasicObject"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    ComplexObject2::class, name="ComplexObject")])
interface BasicUnion2

/**
 * Some basic description
 */
data class BasicObject2(
  /**
   * Object name
   */
  val name: String
) : BasicUnion2

/**
 * Multi line description of a complex type.
 * This is a second line of the paragraph.
 * This is final line of the description.
 */
data class ComplexObject2(
  /**
   * Some object name
   */
  val name: String
) : BasicUnion2
