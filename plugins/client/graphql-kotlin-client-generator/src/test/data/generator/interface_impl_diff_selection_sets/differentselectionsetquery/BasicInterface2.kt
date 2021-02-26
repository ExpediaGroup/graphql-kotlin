package com.expediagroup.graphql.generated.differentselectionsetquery

import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Very basic interface
 */
@Serializable
sealed class BasicInterface2 {
  /**
   * Unique identifier of an interface
   */
  abstract val id: Int
}

/**
 * Example interface implementation where value is an integer
 */
@Serializable
@SerialName(value = "FirstInterfaceImplementation")
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
) : BasicInterface2()

/**
 * Example interface implementation where value is a float
 */
@Serializable
@SerialName(value = "SecondInterfaceImplementation")
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
) : BasicInterface2()
