package com.expediagroup.graphql.generated.differentselectionsetquery

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
import kotlin.Float
import kotlin.Int
import kotlin.String

/**
 * Very basic interface
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "__typename"
)
@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    FirstInterfaceImplementation2::class,
    name="FirstInterfaceImplementation"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    SecondInterfaceImplementation2::class, name="SecondInterfaceImplementation")])
interface BasicInterface2 {
  /**
   * Unique identifier of an interface
   */
  abstract val id: Int
}

/**
 * Example interface implementation where value is an integer
 */
data class FirstInterfaceImplementation2(
  /**
   * Unique identifier of the first implementation
   */
  override val id: Int,
  /**
   * Name of the first implementation
   */
  val name: String,
  /**
   * Custom field integer value
   */
  val intValue: Int
) : BasicInterface2

/**
 * Example interface implementation where value is a float
 */
data class SecondInterfaceImplementation2(
  /**
   * Unique identifier of the second implementation
   */
  override val id: Int,
  /**
   * Name of the second implementation
   */
  val name: String,
  /**
   * Custom field float value
   */
  val floatValue: Float
) : BasicInterface2
