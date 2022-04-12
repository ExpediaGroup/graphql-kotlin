/*
 * Copyright 2022 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.plugin.client.generator.types

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.converter.ScalarConverter
import com.expediagroup.graphql.plugin.client.generator.GraphQLScalar
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.joinToCode

internal const val OPTIONAL_SCALAR_INPUT_JACKSON_SERIALIZER_NAME = "OptionalScalarInputSerializer"

/**
 * Generates custom OptionalInput serializer that works for custom scalars.
 */
internal fun generateJacksonOptionalInputScalarSerializer(customScalars: Collection<GraphQLScalar>): TypeSpec {
    val jacksonOptionalInput = ClassName("com.expediagroup.graphql.client.jackson.types", "OptionalInput").parameterizedBy(STAR)
    val jacksonDefinedInput = MemberName("com.expediagroup.graphql.client.jackson.types", "OptionalInput.Defined")
    val jacksonUndefinedInput = MemberName("com.expediagroup.graphql.client.jackson.types", "OptionalInput.Undefined")

    return TypeSpec.classBuilder(OPTIONAL_SCALAR_INPUT_JACKSON_SERIALIZER_NAME)
        .superclass(JsonSerializer::class.asClassName().parameterizedBy(jacksonOptionalInput))
        .addAnnotation(Generated::class)
        .also { builder ->
            val convertersInitBlock = CodeBlock.builder()
                .add(CodeBlock.of("%M(", MemberName("kotlin.collections", "mapOf")))
                .add(
                    customScalars.map { scalarInfo ->
                        CodeBlock.of("%T::class.java to %T()", scalarInfo.className, scalarInfo.converterClassName)
                    }.joinToCode()
                )
                .add(")")
                .build()
            val converterMapProperty = PropertySpec.builder(
                "converters",
                Map::class.asClassName().parameterizedBy(
                    Class::class.asClassName().parameterizedBy(STAR),
                    ScalarConverter::class.asClassName().parameterizedBy(STAR)
                ),
                KModifier.PRIVATE
            ).initializer(convertersInitBlock)
                .build()
            builder.addProperty(converterMapProperty)
        }
        .addFunction(
            FunSpec.builder("isEmpty")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("provider", SerializerProvider::class.java)
                .addParameter("value", jacksonOptionalInput)
                .returns(BOOLEAN)
                .addStatement("return value == %M", jacksonUndefinedInput)
                .build()
        )
        .addFunction(
            FunSpec.builder("serialize")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("value", jacksonOptionalInput)
                .addParameter("gen", JsonGenerator::class.java)
                .addParameter("serializers", SerializerProvider::class.java)
                .addCode(
                    CodeBlock.of(
                        """when (value) {
                        |  is %M -> return
                        |  is %M -> {
                        |    val rawValue = value.value
                        |    when (rawValue) {
                        |      null -> serializers.defaultNullValueSerializer.serialize(rawValue, gen, serializers)
                        |      is List<*> -> {
                        |        gen.writeStartArray()
                        |        rawValue.filterNotNull().forEach { entry ->
                        |          serializeValue(entry, gen, serializers)
                        |        }
                        |        gen.writeEndArray()
                        |      }
                        |      else -> serializeValue(rawValue, gen, serializers)
                        |    }
                        |  }
                        |}
                        """.trimMargin(),
                        jacksonUndefinedInput, jacksonDefinedInput
                    )
                )
                .build()
        )
        .addFunction(
            FunSpec.builder("serializeValue")
                .addModifiers(KModifier.PRIVATE)
                .addParameter("value", ANY)
                .addParameter("gen", JsonGenerator::class.java)
                .addParameter("serializers", SerializerProvider::class.java)
                .addCode(
                    CodeBlock.of(
                        """val clazz = value::class.java
                        |val converter = converters[clazz] as? ScalarConverter<Any>
                        |if (converter != null) {
                        |  serializers.defaultSerializeValue(converter.toJson(value), gen)
                        |} else {
                        |  serializers.findValueSerializer(clazz).serialize(value, gen, serializers)
                        |}
                        """.trimMargin()
                    )
                )
                .build()
        )
        .build()
}
