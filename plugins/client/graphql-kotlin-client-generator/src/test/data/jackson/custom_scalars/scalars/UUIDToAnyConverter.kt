package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter
import com.fasterxml.jackson.databind.util.StdConverter
import java.util.UUID
import kotlin.Any

@Generated
public class UUIDToAnyConverter : StdConverter<UUID, Any>() {
  private val converter: UUIDScalarConverter = UUIDScalarConverter()

  public override fun convert(`value`: UUID): Any = converter.toJson(value)
}
