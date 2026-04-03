package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.converter.ScalarConverter
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Defined
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.plugin.client.generator.ULocaleScalarConverter
import com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter
import com.ibm.icu.util.ULocale
import java.lang.Class
import java.util.UUID
import kotlin.Any
import kotlin.Boolean
import kotlin.collections.Map
import kotlin.collections.mapOf
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

@Generated
public class OptionalScalarInputSerializer : ValueSerializer<OptionalInput<*>>() {
  private val converters: Map<Class<*>, ScalarConverter<*>> = mapOf(UUID::class.java to
      UUIDScalarConverter(), ULocale::class.java to ULocaleScalarConverter())

  override fun isEmpty(ctxt: SerializationContext, `value`: OptionalInput<*>): Boolean = value ==
      OptionalInput.Undefined

  override fun serialize(
    `value`: OptionalInput<*>,
    gen: JsonGenerator,
    ctxt: SerializationContext,
  ) {
    when (value) {
      is OptionalInput.Undefined -> return
      is OptionalInput.Defined -> {
        val rawValue = value.value
        when (rawValue) {
          null -> ctxt.defaultNullValueSerializer.serialize(rawValue, gen, ctxt)
          is List<*> -> {
            gen.writeStartArray()
            rawValue.filterNotNull().forEach { entry ->
              serializeValue(entry, gen, ctxt)
            }
            gen.writeEndArray()
          }
          else -> serializeValue(rawValue, gen, ctxt)
        }
      }
    }
  }

  private fun serializeValue(
    `value`: Any,
    gen: JsonGenerator,
    ctxt: SerializationContext,
  ) {
    val clazz = value::class.java
    val converter = converters[clazz] as? ScalarConverter<Any>
    if (converter != null) {
      ctxt.writeValue(gen, converter.toJson(value))
    } else {
      ctxt.findValueSerializer(clazz).serialize(value, gen, ctxt)
    }
  }
}
