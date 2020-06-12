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
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.Directives.DeprecatedDirective
import graphql.language.EnumTypeDefinition
import graphql.language.StringValue

/**
 * Generate enum [TypeSpec] from the specified GraphQL enum definition. Adds default `__UNKNOWN_VALUE` relying on Jackson annotation to handle new/unknown enum values.
 */
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
        enumValueDefinition.getDirective(DeprecatedDirective.name)?.let { deprecatedDirective ->
            val deprecatedReason = deprecatedDirective.getArgument("reason")?.value as? StringValue
            val reason = deprecatedReason?.value ?: "no longer supported"
            enumValueTypeSpecBuilder.addAnnotation(
                AnnotationSpec.Companion.builder(Deprecated::class)
                    .addMember("message = %S", reason)
                    .build()
            )
        }
        enumTypeSpecBuilder.addEnumConstant(enumValueDefinition.name, enumValueTypeSpecBuilder.build())
    }

    val unkownTypeSpec = TypeSpec.anonymousClassBuilder()
        .addKdoc("This is a default enum value that will be used when attempting to deserialize unknown value.")
        .addAnnotation(JsonEnumDefaultValue::class)
        .build()

    enumTypeSpecBuilder.addEnumConstant("__UNKNOWN_VALUE", unkownTypeSpec)

    val enumTypeSpec = enumTypeSpecBuilder.build()
    context.typeSpecs[enumDefinition.name] = enumTypeSpec
    return enumTypeSpec
}
