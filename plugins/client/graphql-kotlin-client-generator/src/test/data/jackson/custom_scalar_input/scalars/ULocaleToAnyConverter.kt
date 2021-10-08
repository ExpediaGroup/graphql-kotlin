package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.ULocaleScalarConverter
import com.fasterxml.jackson.databind.util.StdConverter
import com.ibm.icu.util.ULocale
import kotlin.Any

@Generated
public class ULocaleToAnyConverter : StdConverter<ULocale, Any>() {
  private val converter: ULocaleScalarConverter = ULocaleScalarConverter()

  public override fun convert(`value`: ULocale): Any = converter.toJson(value)
}
