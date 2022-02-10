package com.expediagroup.scalars.converters

import com.expediagroup.graphql.client.converter.ScalarConverter
import com.ibm.icu.util.ULocale

class ULocaleScalarConverter : ScalarConverter<ULocale> {
    override fun toScalar(rawValue: Any): ULocale = ULocale(rawValue.toString())
    override fun toJson(value: ULocale): Any = value.toString()
}
