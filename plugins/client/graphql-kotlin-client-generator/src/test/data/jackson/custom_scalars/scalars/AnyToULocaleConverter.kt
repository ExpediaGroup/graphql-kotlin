package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.ULocaleScalarConverter
import com.ibm.icu.util.ULocale
import kotlin.Any
import tools.jackson.databind.util.StdConverter

@Generated
public class AnyToULocaleConverter : StdConverter<Any, ULocale>() {
  private val converter: ULocaleScalarConverter = ULocaleScalarConverter()

  override fun convert(`value`: Any): ULocale = converter.toScalar(value)
}
