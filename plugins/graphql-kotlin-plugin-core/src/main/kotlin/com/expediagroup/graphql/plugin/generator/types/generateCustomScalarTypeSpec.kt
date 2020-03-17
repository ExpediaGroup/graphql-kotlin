package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.fasterxml.jackson.annotation.JsonValue
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.ScalarTypeDefinition

fun generateCustomScalarTypeSpec(context: GraphQLClientGeneratorContext, scalarTypeDefinition: ScalarTypeDefinition): TypeSpec {
    val scalarTypeSpec = TypeSpec.classBuilder(scalarTypeDefinition.name)
    scalarTypeDefinition.description?.content?.let { kdoc ->
        scalarTypeSpec.addKdoc(kdoc)
    }
    scalarTypeSpec.addProperty(PropertySpec.builder("value", String::class)
        .addAnnotation(JsonValue::class.java)
        .build())

    val scalar = scalarTypeSpec.build()
    context.typeSpecs[scalarTypeDefinition.name] = scalar
    return scalar
}
