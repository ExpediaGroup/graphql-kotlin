package com.expediagroup.graphql.generated.interfacewithinlinefragmentsquery

import com.expediagroup.graphql.client.Generated
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Very basic interface
 */
@Generated
@Serializable
public sealed class BasicInterface {
  /**
   * Unique identifier of an interface
   */
  public abstract val id: Int

  /**
   * Name field
   */
  public abstract val name: String
}

/**
 * Example interface implementation where value is an integer
 */
@Generated
@Serializable
@SerialName(value = "FirstInterfaceImplementation")
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
) : BasicInterface()

/**
 * Example interface implementation where value is a float
 */
@Generated
@Serializable
@SerialName(value = "SecondInterfaceImplementation")
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
) : BasicInterface()
