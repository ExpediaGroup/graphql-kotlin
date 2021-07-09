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

import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.client.generator.GraphQLScalar
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.jvm.jvmStatic
import graphql.language.ScalarTypeDefinition
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.jsonPrimitive

/**
 * Generate [TypeSpec] data class with single typed value for the target custom scalar. In order to simplify the serialization/deserialization we are using single value wrapper class that uses
 * Jackson [JsonValue] and [JsonCreator] annotations [com.expediagroup.graphql.client.converter.ScalarConverter] to convert between raw JSON String representation and Kotlin type safe value.
 *
 * @see generateGraphQLCustomScalarTypeAlias for default handling of scalars
 */
internal fun generateGraphQLCustomScalarTypeSpec(
    context: GraphQLClientGeneratorContext,
    scalarTypeDefinition: ScalarTypeDefinition,
    customScalarKotlin: GraphQLScalar
): TypeSpec {
    val customScalarName = scalarTypeDefinition.name

    val scalarTypeSpec = TypeSpec.classBuilder(customScalarName)
        .addModifiers(KModifier.DATA)
    scalarTypeDefinition.description?.content?.let { kdoc ->
        scalarTypeSpec.addKdoc("%L", kdoc)
    }

    val scalarValue = PropertySpec.builder("value", customScalarKotlin.type.toClassName())
        .initializer("value")
        .build()
    scalarTypeSpec.addProperty(scalarValue)

    val constructor = FunSpec.constructorBuilder()
        .addParameter(scalarValue.name, scalarValue.type)
        .build()
    scalarTypeSpec.primaryConstructor(constructor)

    if (context.serializer == GraphQLSerializer.KOTLINX) {
        val serializerClassName = ClassName("${context.packageName}.scalars", "${customScalarName}Serializer")
        scalarTypeSpec.addAnnotation(
            AnnotationSpec.builder(Serializable::class)
                .addMember("with = %T::class", serializerClassName)
                .build()
        )
    } else {
        val converterClassName = customScalarKotlin.converter.toClassName()
        val converter = PropertySpec.builder("converter", converterClassName)
            .initializer("%T()", converterClassName)
            .build()
        scalarTypeSpec.addFunction(
            FunSpec.builder("rawValue")
                .addAnnotation(JsonValue::class.java)
                .addStatement("return %N.toJson(value)", converter)
                .build()
        )
        scalarTypeSpec.addType(
            TypeSpec.companionObjectBuilder()
                .addProperty(converter)
                .addFunction(
                    FunSpec.builder("create")
                        .addAnnotation(JsonCreator::class.java)
                        .jvmStatic()
                        .addParameter("rawValue", Any::class)
                        .addStatement("return %L(%N.toScalar(rawValue))", customScalarName, converter)
                        .build()
                )
                .build()
        )
    }

    return scalarTypeSpec.build()
}

internal fun generateGraphQLCustomScalarKSerializer(
    scalarTypeDefinition: ScalarTypeDefinition,
    converterType: String,
    scalarWrapperClassName: ClassName
): TypeSpec {
    val customScalarName = scalarTypeDefinition.name
    val serializerName = "${customScalarName}Serializer"
    val serializerTypeSpec = TypeSpec.classBuilder(serializerName)
        .addSuperinterface(KSerializer::class.asTypeName().parameterizedBy(scalarWrapperClassName))

    val converterClassName = converterType.toClassName()
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
        .addParameter("value", scalarWrapperClassName)
        .addStatement("val encoded = converter.toJson(value.value)")
        .addStatement("encoder.encodeString(encoded.toString())")
        .build()
    serializerTypeSpec.addFunction(serializeFun)

    val jsonDecoder = ClassName("kotlinx.serialization.json", "JsonDecoder")
    val jsonPrimitive = ClassName("kotlinx.serialization.json", "jsonPrimitive")
    val deserializeFun = FunSpec.builder("deserialize")
        .addModifiers(KModifier.OVERRIDE)
        .returns(scalarWrapperClassName)
        .addParameter("decoder", Decoder::class)
        .addStatement("val jsonDecoder = decoder as %T", jsonDecoder)
        .addStatement("val element = jsonDecoder.decodeJsonElement()")
        .addStatement("val rawContent = element.%T.content", jsonPrimitive)
        .addStatement("return %T(value = converter.toScalar(rawContent))", scalarWrapperClassName)
        .build()
    serializerTypeSpec.addFunction(deserializeFun)

    return serializerTypeSpec.build()
}

internal fun String.toClassName(): ClassName {
    val index = this.lastIndexOf('.')
    return if (index < 0) {
        ClassName("", this)
    } else {
        ClassName(this.substring(0, index), this.substring(index + 1))
    }
}
