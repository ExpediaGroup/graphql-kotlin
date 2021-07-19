package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter
import com.fasterxml.jackson.databind.util.StdConverter
import java.util.UUID
import kotlin.String

public class UUIDToStringConverter : StdConverter<UUID, String>() {
  private val converter: UUIDScalarConverter = UUIDScalarConverter()

  public override fun convert(`value`: UUID): String = converter.toJson(value)
}
