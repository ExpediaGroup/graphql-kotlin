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
  defaultImpl = DefaultBasicInterfaceImplementation::class
)
@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    FirstInterfaceImplementation::class,
    name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    SecondInterfaceImplementation::class, name="SecondInterfaceImplementation")])
public interface BasicInterface {
  /**
   * Unique identifier of an interface
   */
  public val id: Int

  /**
   * Name field
   */
  public val name: String
}

/**
 * Example interface implementation where value is an integer
 */
@Generated
public data class FirstInterfaceImplementation(
  /**
   * Unique identifier of the first implementation
   */
  public override val id: Int,
  /**
   * Name of the first implementation
   */
  public override val name: String,
  /**
   * Custom field integer value
   */
  public val intValue: Int
) : BasicInterface

/**
 * Example interface implementation where value is a float
 */
@Generated
public data class SecondInterfaceImplementation(
  /**
   * Unique identifier of the second implementation
   */
  public override val id: Int,
  /**
   * Name of the second implementation
   */
  public override val name: String,
  /**
   * Custom field float value
   */
  public val floatValue: Double
) : BasicInterface

/**
 * Fallback BasicInterface implementation that will be used when unknown/unhandled type is
 * encountered.
 */
@Generated
public data class DefaultBasicInterfaceImplementation(
  /**
   * Unique identifier of an interface
   */
  public override val id: Int,
  /**
   * Name field
   */
  public override val name: String
) : BasicInterface
