package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter
import java.util.UUID
import kotlin.Any
import tools.jackson.databind.util.StdConverter

@Generated
public class UUIDToAnyConverter : StdConverter<UUID, Any>() {
  private val converter: UUIDScalarConverter = UUIDScalarConverter()

  override fun convert(`value`: UUID): Any = converter.toJson(value)
}
