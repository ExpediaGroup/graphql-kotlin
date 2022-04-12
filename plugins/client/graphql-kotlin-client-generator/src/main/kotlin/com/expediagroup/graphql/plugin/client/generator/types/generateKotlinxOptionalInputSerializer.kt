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
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Generates custom optional input serializer for given element.
 */
internal fun generateKotlinxOptionalInputSerializer(
    elementClassName: ClassName,
    typeName: String,
    customSerializerClassName: ClassName? = null,
    isList: Boolean = false,
): TypeSpec {
    val optionalClassName = if (isList) {
        LIST.parameterizedBy(elementClassName)
    } else {
        elementClassName
    }
    val kotlinxOptionalInput = ClassName("com.expediagroup.graphql.client.serialization.types", "OptionalInput").parameterizedBy(optionalClassName)
    val kotlinxDefinedInput = ClassName("com.expediagroup.graphql.client.serialization.types", "OptionalInput.Defined").parameterizedBy(optionalClassName)
    val kotlinxUndefinedInput = ClassName("com.expediagroup.graphql.client.serialization.types", "OptionalInput.Undefined")
    val kSerializer = ClassName("kotlinx.serialization", "KSerializer").parameterizedBy(optionalClassName)

    return TypeSpec.objectBuilder(typeName)
        .addSuperinterface(KSerializer::class.asClassName().parameterizedBy(kotlinxOptionalInput))
        .addAnnotation(Generated::class)
        .addProperty(
            PropertySpec.builder("delegate", kSerializer, KModifier.PRIVATE)
                .initializer(delegateInitializer(elementClassName, customSerializerClassName, isList))
                .build()
        )
        .addProperty(
            PropertySpec.builder("descriptor", SerialDescriptor::class.asTypeName(), KModifier.OVERRIDE)
                .initializer(
                    // TODO drop serializer suffix
                    CodeBlock.of("%M(\"$typeName\")", MemberName("kotlinx.serialization.descriptors", "buildClassSerialDescriptor"))
                )
                .build()
        )
        .addFunction(
            FunSpec.builder("serialize")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("encoder", Encoder::class.asTypeName())
                .addParameter("value", kotlinxOptionalInput)
                .addCode(
                    CodeBlock.of(
                        """when (value) {
                        |  is %T -> return
                        |  is %T ->
                        |    encoder.encodeNullableSerializableValue(delegate, value.value)
                        |}
                        """.trimMargin(),
                        kotlinxUndefinedInput,
                        kotlinxDefinedInput
                    )
                )
                .build()
        )
        .addFunction(
            FunSpec.builder("deserialize")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("decoder", Decoder::class.asTypeName())
                .addKdoc("undefined is only supported during client serialization, this code should never be invoked")
                .returns(kotlinxOptionalInput)
                .addCode(
                    CodeBlock.of("return OptionalInput.Defined(decoder.decodeNullableSerializableValue(delegate.%M))", MemberName("kotlinx.serialization.builtins", "nullable"))
                )
                .build()
        )
        .build()
}

private fun delegateInitializer(
    elementClassName: ClassName,
    customSerializer: ClassName? = null,
    isList: Boolean = false
): CodeBlock = when {
    isList -> CodeBlock.builder()
        .add("%M(", MemberName("kotlinx.serialization.builtins", "ListSerializer"))
        .add(delegateInitializer(elementClassName, customSerializer, isList = false))
        .add(")")
        .build()
    customSerializer != null -> CodeBlock.of("%T", customSerializer)
    else -> CodeBlock.of("%T.serializer()", elementClassName)
}
