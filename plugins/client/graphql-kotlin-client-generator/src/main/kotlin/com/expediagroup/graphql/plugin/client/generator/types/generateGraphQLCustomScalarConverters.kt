/*
 * Copyright 2021 Expedia, Inc
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
import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import com.expediagroup.graphql.plugin.client.generator.ScalarConverterInfo
import com.fasterxml.jackson.databind.util.StdConverter
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Generate [ScalarConverterInfo] data class that holds information about generated scalar Jackson converters/or kotlinx-serialization serializer.
 * Generated converters/serializer utilize provided [com.expediagroup.graphql.client.converter.ScalarConverter] to convert between raw JSON String
 * representation and Kotlin type safe value.
 *
 * @see generateGraphQLCustomScalarTypeAlias for default handling of scalars
 */
internal fun generateGraphQLCustomScalarConverters(
    context: GraphQLClientGeneratorContext,
    scalarClassName: ClassName,
    converterClassName: ClassName
): ScalarConverterInfo {
    return if (context.serializer == GraphQLSerializer.JACKSON) {
        generateGraphQLCustomScalarJacksonConverters(context, scalarClassName, converterClassName)
    } else {
        generateGraphQLCustomScalarKSerializer(context, scalarClassName, converterClassName)
    }
}

private fun generateGraphQLCustomScalarJacksonConverters(
    context: GraphQLClientGeneratorContext,
    scalarClassName: ClassName,
    converterClassName: ClassName
): ScalarConverterInfo {
    val customScalarName = scalarClassName.simpleName
    val converter = PropertySpec.builder("converter", converterClassName)
        .initializer("%T()", converterClassName)
        .addModifiers(KModifier.PRIVATE)
        .build()

    val serializeConverterName = "${customScalarName}ToAnyConverter"
    val serializerConverterTypeSpec = TypeSpec.classBuilder(serializeConverterName)
        .superclass(StdConverter::class.asTypeName().parameterizedBy(scalarClassName, ANY))
        .addAnnotation(Generated::class)
        .addProperty(converter)
        .addFunction(
            FunSpec.builder("convert")
                .addModifiers(KModifier.OVERRIDE)
                .returns(ANY)
                .addParameter("value", scalarClassName)
                .addStatement("return converter.toJson(value)")
                .build()
        )
        .build()

    val deserializeConverterName = "AnyTo${customScalarName}Converter"
    val deserializerConverterTypeSpec = TypeSpec.classBuilder(deserializeConverterName)
        .superclass(StdConverter::class.asTypeName().parameterizedBy(ANY, scalarClassName))
        .addAnnotation(Generated::class)
        .addProperty(converter)
        .addFunction(
            FunSpec.builder("convert")
                .addModifiers(KModifier.OVERRIDE)
                .returns(scalarClassName)
                .addParameter("value", ANY)
                .addStatement("return converter.toScalar(value)")
                .build()
        )
        .build()
    return ScalarConverterInfo.JacksonConvertersInfo(
        serializerClassName = ClassName("${context.packageName}.scalars", serializeConverterName),
        serializerTypeSpec = serializerConverterTypeSpec,
        deserializerClassName = ClassName("${context.packageName}.scalars", deserializeConverterName),
        deserializerTypeSpec = deserializerConverterTypeSpec
    )
}

private fun generateGraphQLCustomScalarKSerializer(
    context: GraphQLClientGeneratorContext,
    scalarClassName: ClassName,
    converterClassName: ClassName
): ScalarConverterInfo {
    val customScalarName = scalarClassName.simpleName
    val serializerName = "${customScalarName}Serializer"
    val serializerTypeSpec = TypeSpec.objectBuilder(serializerName)
        .addSuperinterface(KSerializer::class.asTypeName().parameterizedBy(scalarClassName))
        .addAnnotation(Generated::class)

    val converter = PropertySpec.builder("converter", converterClassName)
        .initializer("%T()", converterClassName)
        .addModifiers(KModifier.PRIVATE)
        .build()
    serializerTypeSpec.addProperty(converter)

    val primitiveSerialDescriptor = MemberName("kotlinx.serialization.descriptors", "PrimitiveSerialDescriptor")
    val stringKind = MemberName(PrimitiveKind::class.asClassName(), "STRING")
    val descriptor = PropertySpec.builder("descriptor", SerialDescriptor::class)
        .initializer("%M(%S, %M)", primitiveSerialDescriptor, customScalarName, stringKind)
        .addModifiers(KModifier.OVERRIDE)
        .build()
    serializerTypeSpec.addProperty(descriptor)

    val serializeFun = FunSpec.builder("serialize")
        .addModifiers(KModifier.OVERRIDE)
        .addParameter("encoder", Encoder::class)
        .addParameter("value", scalarClassName)
        .addStatement("val encoded = converter.toJson(value)")
        .addStatement("encoder.encodeString(encoded.toString())")
        .build()
    serializerTypeSpec.addFunction(serializeFun)

    val jsonDecoder = ClassName("kotlinx.serialization.json", "JsonDecoder")
    val jsonPrimitive = ClassName("kotlinx.serialization.json", "jsonPrimitive")
    val deserializeFun = FunSpec.builder("deserialize")
        .addModifiers(KModifier.OVERRIDE)
        .returns(scalarClassName)
        .addParameter("decoder", Decoder::class)
        .addStatement("val jsonDecoder = decoder as %T", jsonDecoder)
        .addStatement("val element = jsonDecoder.decodeJsonElement()")
        .addStatement("val rawContent = element.%T.content", jsonPrimitive)
        .addStatement("return converter.toScalar(rawContent)")
        .build()
    serializerTypeSpec.addFunction(deserializeFun)

    return ScalarConverterInfo.KotlinxSerializerInfo(
        serializerClassName = ClassName("${context.packageName}.scalars", serializerName),
        serializerTypeSpec = serializerTypeSpec.build()
    )
}
