package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import kotlin.Any
import kotlin.jvm.JvmStatic

/**
 * Custom scalar representing UUID
 */
data class UUID(
  val value: java.util.UUID
) {
  @JsonValue
  fun rawValue() = converter.toJson(value)

  companion object {
    val converter: UUIDScalarConverter = UUIDScalarConverter()

    @JsonCreator
    @JvmStatic
    fun create(rawValue: Any) = UUID(converter.toScalar(rawValue))
  }
}
