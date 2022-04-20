package com.expediagroup.graphql.generated.differentselectionsetquery

import com.expediagroup.graphql.client.Generated
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
   * Unique identifier of an interface
   */
  public val id: Int
}

/**
 * Example interface implementation where value is an integer
 */
@Generated
public data class FirstInterfaceImplementation2(
  /**
   * Unique identifier of the first implementation
   */
  public override val id: Int,
  /**
   * Name of the first implementation
   */
  public val name: String,
  /**
   * Custom field integer value
   */
  public val intValue: Int,
) : BasicInterface2

/**
 * Example interface implementation where value is a float
 */
@Generated
public data class SecondInterfaceImplementation2(
  /**
   * Unique identifier of the second implementation
   */
  public override val id: Int,
  /**
   * Name of the second implementation
   */
  public val name: String,
  /**
   * Custom field float value
   */
  public val floatValue: Double,
) : BasicInterface2

/**
 * Fallback BasicInterface2 implementation that will be used when unknown/unhandled type is
 * encountered.
 */
@Generated
public data class DefaultBasicInterface2Implementation(
  /**
   * Unique identifier of an interface
   */
  public override val id: Int,
) : BasicInterface2
