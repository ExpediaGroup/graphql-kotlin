package com.expediagroup.graphql.generated.scalars

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.converter.ScalarConverter
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Defined
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.plugin.client.generator.ULocaleScalarConverter
import com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.ibm.icu.util.ULocale
import java.lang.Class
import java.util.UUID
import kotlin.Any
import kotlin.Boolean
import kotlin.Unit
import kotlin.collections.Map
import kotlin.collections.mapOf

@Generated
public class OptionalScalarInputSerializer : JsonSerializer<OptionalInput<*>>() {
  private val converters: Map<Class<*>, ScalarConverter<*>> = mapOf(UUID::class.java to
      UUIDScalarConverter(), ULocale::class.java to ULocaleScalarConverter())

  public override fun isEmpty(provider: SerializerProvider, `value`: OptionalInput<*>): Boolean =
      value == OptionalInput.Undefined

  public override fun serialize(
    `value`: OptionalInput<*>,
    gen: JsonGenerator,
    serializers: SerializerProvider,
  ): Unit {
    when (value) {
      is OptionalInput.Undefined -> return
      is OptionalInput.Defined -> {
        val rawValue = value.value
        when (rawValue) {
          null -> serializers.defaultNullValueSerializer.serialize(rawValue, gen, serializers)
          is List<*> -> {
            gen.writeStartArray()
            rawValue.filterNotNull().forEach { entry ->
              serializeValue(entry, gen, serializers)
            }
            gen.writeEndArray()
          }
          else -> serializeValue(rawValue, gen, serializers)
        }
      }
    }
  }

  private fun serializeValue(
    `value`: Any,
    gen: JsonGenerator,
    serializers: SerializerProvider,
  ): Unit {
    val clazz = value::class.java
    val converter = converters[clazz] as? ScalarConverter<Any>
    if (converter != null) {
      serializers.defaultSerializeValue(converter.toJson(value), gen)
    } else {
      serializers.findValueSerializer(clazz).serialize(value, gen, serializers)
    }
  }
}
