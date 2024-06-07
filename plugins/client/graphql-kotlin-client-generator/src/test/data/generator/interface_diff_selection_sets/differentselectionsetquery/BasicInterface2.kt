package com.expediagroup.graphql.generated.differentselectionsetquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.Id.NAME
import kotlin.Double
import kotlin.Int
import kotlin.String

/**
 * Very basic interface
 */
@Generated
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "__typename",
  defaultImpl = DefaultBasicInterface2Implementation::class,
)
@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    FirstInterfaceImplementation2::class,
    name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    SecondInterfaceImplementation2::class, name="SecondInterfaceImplementation")])
public interface BasicInterface2 {
  /**
   * Name field
   */
  @get:JsonProperty(value = "name")
  public val name: String
}

/**
 * Example interface implementation where value is an integer
 */
@Generated
public data class FirstInterfaceImplementation2(
  /**
   * Name of the first implementation
   */
  @get:JsonProperty(value = "name")
  override val name: String,
  /**
   * Custom field integer value
   */
  @get:JsonProperty(value = "intValue")
  public val intValue: Int,
) : BasicInterface2

/**
 * Example interface implementation where value is a float
 */
@Generated
public data class SecondInterfaceImplementation2(
  /**
   * Name of the second implementation
   */
  @get:JsonProperty(value = "name")
  override val name: String,
  /**
   * Custom field float value
   */
  @get:JsonProperty(value = "floatValue")
  public val floatValue: Double,
) : BasicInterface2

/**
 * Fallback BasicInterface2 implementation that will be used when unknown/unhandled type is
 * encountered.
 */
@Generated
public data class DefaultBasicInterface2Implementation(
  /**
   * Name field
   */
  @get:JsonProperty(value = "name")
  override val name: String,
) : BasicInterface2
