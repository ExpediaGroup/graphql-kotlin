/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.jvm.jvmStatic
import graphql.language.ScalarTypeDefinition

/**
 * Generate [TypeSpec] data class with single typed value for the target custom scalar. In order to simplify the serialization/deserialization we are using single value wrapper class that uses
 * Jackson [JsonValue] and [JsonCreator] annotations [com.expediagroup.graphql.client.converter.ScalarConverter] to convert between raw JSON String representation and Kotlin type safe value.
 *
 * @see generateGraphQLCustomScalarTypeAlias for default handling of scalars
 */
internal fun generateGraphQLCustomScalarTypeSpec(context: GraphQLClientGeneratorContext, scalarTypeDefinition: ScalarTypeDefinition): TypeSpec {
    val customScalarName = scalarTypeDefinition.name
    // its not possible to enter this method if converter is not available
    val converterMapping = context.scalarTypeToConverterMapping[customScalarName]!!

    val scalarTypeSpec = TypeSpec.classBuilder(customScalarName)
    scalarTypeSpec.addModifiers(KModifier.DATA)
    scalarTypeDefinition.description?.content?.let { kdoc ->
        scalarTypeSpec.addKdoc(kdoc)
    }

    val scalarValue = PropertySpec.builder("value", converterMapping.type.toClassName())
        .initializer("value")
        .build()
    scalarTypeSpec.addProperty(scalarValue)

    val constructor = FunSpec.constructorBuilder()
        .addParameter(scalarValue.name, scalarValue.type)
        .build()
    scalarTypeSpec.primaryConstructor(constructor)

    val converterClassName = converterMapping.converter.toClassName()
    val converter = PropertySpec.builder("converter", converterClassName)
        .initializer("%T()", converterClassName)
        .build()
    scalarTypeSpec.addFunction(FunSpec.builder("rawValue")
        .addAnnotation(JsonValue::class.java)
        .addStatement("return %N.toJson(value)", converter)
        .build())
    scalarTypeSpec.addType(TypeSpec.companionObjectBuilder()
        .addProperty(converter)
        .addFunction(FunSpec.builder("create")
            .addAnnotation(JsonCreator::class.java)
            .jvmStatic()
            .addParameter("rawValue", String::class)
            .addStatement("return %L(%N.toScalar(rawValue))", customScalarName, converter)
            .build())
        .build())

    val scalar = scalarTypeSpec.build()
    context.typeSpecs[scalarTypeDefinition.name] = scalar
    return scalar
}

private fun String.toClassName(): ClassName {
    val index = this.lastIndexOf('.')
    return if (index < 0) {
        ClassName("", this)
    } else {
        ClassName(this.substring(0, index), this.substring(index + 1))
    }
}

