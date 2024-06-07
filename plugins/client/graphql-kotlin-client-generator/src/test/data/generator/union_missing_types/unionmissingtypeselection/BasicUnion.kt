package com.expediagroup.graphql.generated.unionmissingtypeselection

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
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
    BasicObject::class, name="BasicObject")])
public interface BasicUnion

/**
 * Some basic description
 */
@Generated
public data class BasicObject(
  @get:JsonProperty(value = "id")
  public val id: Int,
  /**
   * Object name
   */
  @get:JsonProperty(value = "name")
  public val name: String,
) : BasicUnion

/**
 * Fallback BasicUnion implementation that will be used when unknown/unhandled type is encountered.
 */
@Generated
public class DefaultBasicUnionImplementation() : BasicUnion
