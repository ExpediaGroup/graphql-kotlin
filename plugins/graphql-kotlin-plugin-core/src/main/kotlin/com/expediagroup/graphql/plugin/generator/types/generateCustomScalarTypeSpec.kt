package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.jvm.jvmStatic
import graphql.language.ScalarTypeDefinition

fun generateCustomScalarTypeSpec(context: GraphQLClientGeneratorContext, scalarTypeDefinition: ScalarTypeDefinition): TypeSpec {
    val customScalarName = scalarTypeDefinition.name
    val scalarTypeSpec = TypeSpec.classBuilder(customScalarName)
    scalarTypeDefinition.description?.content?.let { kdoc ->
        scalarTypeSpec.addKdoc(kdoc)
    }
    // TODO support non-string values
    scalarTypeSpec.addProperty(PropertySpec.builder("value", String::class)
        .addAnnotation(JsonValue::class.java)
        .build())
    scalarTypeSpec.addType(TypeSpec.companionObjectBuilder()
        .addFunction(FunSpec.builder("create")
            .addAnnotation(JsonCreator::class.java)
            .jvmStatic()
            .addParameter("stringValue", String::class)
            .addStatement("return %S(stringValue)", customScalarName)
            .build())
        .build())

    val scalar = scalarTypeSpec.build()
    context.typeSpecs[scalarTypeDefinition.name] = scalar
    return scalar
}
