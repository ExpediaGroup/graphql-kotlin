package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.directives.DEPRECATED_DIRECTIVE_NAME
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.EnumTypeDefinition

internal fun generateGraphQLEnumTypeSpec(context: GraphQLClientGeneratorContext, enumDefinition: EnumTypeDefinition): TypeSpec {
    val enumTypeSpecBuilder = TypeSpec.enumBuilder(enumDefinition.name)
    enumDefinition.description?.content?.let { kdoc ->
        enumTypeSpecBuilder.addKdoc(kdoc)
    }
    enumDefinition.enumValueDefinitions.forEach { enumValueDefinition ->
        val enumValueTypeSpecBuilder = TypeSpec.anonymousClassBuilder()
        enumValueDefinition.description?.content?.let { kdoc ->
            enumValueTypeSpecBuilder.addKdoc(kdoc)
        }
        enumValueDefinition.getDirective(DEPRECATED_DIRECTIVE_NAME)?.let { deprecatedDirective ->
            enumValueTypeSpecBuilder.addAnnotation(AnnotationSpec.Companion.builder(Deprecated::class)
                .addMember("message = %S", deprecatedDirective.getArgument("reason"))
                .build())
        }
        enumTypeSpecBuilder.addEnumConstant(enumValueDefinition.name, enumValueTypeSpecBuilder.build())
    }
    enumTypeSpecBuilder.addEnumConstant("__UNKNOWN_VALUE", TypeSpec.anonymousClassBuilder()
        .addKdoc("This is a default enum value that will be used when attempting to deserialize unknown value.")
        .addAnnotation(JsonEnumDefaultValue::class)
        .build())

    val enumTypeSpec = enumTypeSpecBuilder.build()
    context.typeSpecs[enumDefinition.name] = enumTypeSpec
    return enumTypeSpec
}
