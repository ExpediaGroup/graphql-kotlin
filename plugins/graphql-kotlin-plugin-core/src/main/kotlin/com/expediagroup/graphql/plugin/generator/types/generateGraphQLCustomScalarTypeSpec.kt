package com.expediagroup.graphql.plugin.generator.types

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
    // its not possible to enter this method if converter is not available
    val converterMapping = context.scalarTypeToConverterMapping[customScalarName]!!

    val scalarTypeSpec = TypeSpec.classBuilder(customScalarName)
    scalarTypeDefinition.description?.content?.let { kdoc ->
        scalarTypeSpec.addKdoc(kdoc)
    }
    val scalarType = Class.forName(converterMapping.type).kotlin
    val scalarValue = PropertySpec.builder("value", scalarType)
        .initializer("value")
        .build()
    scalarTypeSpec.addProperty(scalarValue)

    val constructor = FunSpec.constructorBuilder()
        .addParameter(scalarValue.name, scalarValue.type)
        .build()
    scalarTypeSpec.primaryConstructor(constructor)

    val converterType = Class.forName(converterMapping.converter).kotlin
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

    val scalar = scalarTypeSpec.build()
    context.typeSpecs[scalarTypeDefinition.name] = scalar
    return scalar
}
