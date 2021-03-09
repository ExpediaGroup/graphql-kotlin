package com.expediagroup.graphql.generated.secondquery

import kotlin.Float
import kotlin.Int
import kotlin.String
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

  /**
   * Name field
   */
  abstract val name: String
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
   * Name of the first implementation
   */
  override val name: String,
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
   * Name of the second implementation
   */
  override val name: String,
  /**
   * Custom field float value
   */
  val floatValue: Float
) : BasicInterface()
