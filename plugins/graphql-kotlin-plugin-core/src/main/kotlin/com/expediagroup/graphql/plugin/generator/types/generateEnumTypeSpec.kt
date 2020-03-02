package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.generator.graphQLComments
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.EnumTypeDefinition

internal fun generateEnumTypeSpec(context: GraphQLClientGeneratorContext, enumDefinition: EnumTypeDefinition): TypeSpec {
    val enumTypeSpecBuilder = TypeSpec.enumBuilder(enumDefinition.name)
    enumDefinition.graphQLComments()?.let { kdoc ->
        enumTypeSpecBuilder.addKdoc(kdoc)
    }
    enumDefinition.enumValueDefinitions.forEach { enumValueDefinition ->
        enumTypeSpecBuilder.addEnumConstant(enumValueDefinition.name)
    }
    enumTypeSpecBuilder.addEnumConstant("_UNKNOWN_VALUE")

    val enumTypeSpec = enumTypeSpecBuilder.build()
    context.typeSpecs.add(enumTypeSpec)
    return enumTypeSpec
}
