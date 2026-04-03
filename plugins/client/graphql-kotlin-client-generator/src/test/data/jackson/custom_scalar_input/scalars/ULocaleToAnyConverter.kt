package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.ULocaleScalarConverter
import com.ibm.icu.util.ULocale
import kotlin.Any
import tools.jackson.databind.util.StdConverter

@Generated
public class ULocaleToAnyConverter : StdConverter<ULocale, Any>() {
  private val converter: ULocaleScalarConverter = ULocaleScalarConverter()

  override fun convert(`value`: ULocale): Any = converter.toJson(value)
}
