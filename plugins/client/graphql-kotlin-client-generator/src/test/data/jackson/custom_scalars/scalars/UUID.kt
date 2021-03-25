package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter
import com.fasterxml.jackson.`annotation`.JsonCreator
import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.Any
import kotlin.jvm.JvmStatic

/**
 * Custom scalar representing UUID
 */
public data class UUID(
  public val value: java.util.UUID
) {
  @JsonValue
  public fun rawValue() = converter.toJson(value)

  public companion object {
    public val converter: UUIDScalarConverter = UUIDScalarConverter()

    @JsonCreator
    @JvmStatic
    public fun create(rawValue: Any) = UUID(converter.toScalar(rawValue))
  }
}
