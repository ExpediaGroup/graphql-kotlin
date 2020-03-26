package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.CustomScalarConverterMapping
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.jvm.jvmStatic
import graphql.language.ScalarTypeDefinition

internal fun generateGraphQLCustomScalarTypeSpec(context: GraphQLClientGeneratorContext, scalarTypeDefinition: ScalarTypeDefinition): TypeSpec {
    val customScalarName = scalarTypeDefinition.name
    val scalarTypeSpec = TypeSpec.classBuilder(customScalarName)
    scalarTypeDefinition.description?.content?.let { kdoc ->
        scalarTypeSpec.addKdoc(kdoc)
    }

    val customConverterMapping = context.scalarTypeToConverterMapping[customScalarName]
    if (customConverterMapping == null) {
        generateDefaultScalar(scalarTypeSpec, customScalarName)
    } else {
        generateScalarWithCustomConverter(scalarTypeSpec, customScalarName, customConverterMapping)
    }

    val scalar = scalarTypeSpec.build()
    context.typeSpecs[scalarTypeDefinition.name] = scalar
    return scalar
}

private fun generateDefaultScalar(scalarTypeSpec: TypeSpec.Builder, customScalarName: String) {
    val scalarValue = PropertySpec.builder("value", String::class)
        .initializer("value")
        .build()
    scalarTypeSpec.addProperty(scalarValue)

    val constructor = FunSpec.constructorBuilder()
        .addParameter(scalarValue.name, scalarValue.type)
        .build()
    scalarTypeSpec.primaryConstructor(constructor)
    scalarTypeSpec.addFunction(FunSpec.builder("rawValue")
        .addAnnotation(JsonValue::class.java)
        .addStatement("return %N", scalarValue)
        .build())
    scalarTypeSpec.addType(TypeSpec.companionObjectBuilder()
        .addFunction(FunSpec.builder("create")
            .addAnnotation(JsonCreator::class.java)
            .jvmStatic()
            .addParameter("rawValue", String::class)
            .addStatement("return %L(rawValue)", customScalarName)
            .build())
        .build())
}

private fun generateScalarWithCustomConverter(scalarTypeSpec: TypeSpec.Builder, customScalarName: String, customConverterMapping: CustomScalarConverterMapping) {
    val scalarType = Class.forName(customConverterMapping.type).kotlin
    val scalarValue = PropertySpec.builder("value", scalarType)
        .initializer("value")
        .build()
    scalarTypeSpec.addProperty(scalarValue)

    val constructor = FunSpec.constructorBuilder()
        .addParameter(scalarValue.name, scalarValue.type)
        .build()
    scalarTypeSpec.primaryConstructor(constructor)

    val converterType = Class.forName(customConverterMapping.converter).kotlin
    val converter = PropertySpec.builder("converter", converterType)
        .initializer("%T()", converterType)
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
}
