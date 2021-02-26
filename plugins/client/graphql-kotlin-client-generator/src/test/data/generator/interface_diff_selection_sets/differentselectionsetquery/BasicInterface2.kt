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
   * Name field
   */
  abstract val name: String
}

/**
 * Example interface implementation where value is an integer
 */
@Serializable
@SerialName(value = "FirstInterfaceImplementation")
data class FirstInterfaceImplementation2(
  /**
   * Name of the first implementation
   */
  override val name: String,
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
   * Name of the second implementation
   */
  override val name: String,
  /**
   * Custom field float value
   */
  val floatValue: Float
) : BasicInterface2()
