package com.expediagroup.graphql.generated.differentselectionsetquery

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.Id.NAME
import kotlin.Float
import kotlin.Int

/**
 * Very basic interface
 */
@Generated
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "__typename"
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
   * Custom field float value
   */
  public val floatValue: Float
) : BasicInterface
