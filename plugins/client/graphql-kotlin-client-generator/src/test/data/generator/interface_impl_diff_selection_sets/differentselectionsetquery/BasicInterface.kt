package com.expediagroup.graphql.generated.differentselectionsetquery

import kotlin.Float
import kotlin.Int
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Very basic interface
 */
@Serializable
sealed class BasicInterface {
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
data class FirstInterfaceImplementation(
  /**
   * Unique identifier of the first implementation
   */
  override val id: Int,
  /**
   * Custom field integer value
   */
  val intValue: Int
) : BasicInterface()

/**
 * Example interface implementation where value is a float
 */
@Serializable
@SerialName(value = "SecondInterfaceImplementation")
data class SecondInterfaceImplementation(
  /**
   * Unique identifier of the second implementation
   */
  override val id: Int,
  /**
   * Custom field float value
   */
  val floatValue: Float
) : BasicInterface()
