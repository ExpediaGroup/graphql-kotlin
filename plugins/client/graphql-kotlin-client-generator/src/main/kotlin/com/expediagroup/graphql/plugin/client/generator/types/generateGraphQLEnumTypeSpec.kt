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

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.Directives.DeprecatedDirective
import graphql.language.EnumTypeDefinition
import graphql.language.StringValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal const val UNKNOWN_VALUE = "__UNKNOWN_VALUE"

/**
 * Generate enum [TypeSpec] from the specified GraphQL enum definition. Adds default `__UNKNOWN_VALUE` relying on Jackson annotation to handle new/unknown enum values.
 */
internal fun generateGraphQLEnumTypeSpec(context: GraphQLClientGeneratorContext, enumDefinition: EnumTypeDefinition): TypeSpec {
    val enumTypeSpecBuilder = TypeSpec.enumBuilder(enumDefinition.name)
        .addAnnotation(Generated::class)
    enumDefinition.description?.content?.let { kdoc ->
        enumTypeSpecBuilder.addKdoc("%L", kdoc)
    }
    enumDefinition.enumValueDefinitions.forEach { enumValueDefinition ->
        val enumValueTypeSpecBuilder = TypeSpec.anonymousClassBuilder()
        enumValueDefinition.description?.content?.let { kdoc ->
            enumValueTypeSpecBuilder.addKdoc("%L", kdoc)
        }
        val deprecatedDirective = enumValueDefinition.getDirectives(DeprecatedDirective.name).firstOrNull()
        if (deprecatedDirective != null) {
            val deprecatedReason = deprecatedDirective.getArgument("reason")?.value as? StringValue
            val reason = deprecatedReason?.value ?: "no longer supported"
            enumValueTypeSpecBuilder.addAnnotation(
                AnnotationSpec.Companion.builder(Deprecated::class)
                    .addMember("message = %S", reason)
                    .build()
            )
        }
        val enumName = enumValueDefinition.name.uppercase()
        if (enumName != enumValueDefinition.name) {
            if (context.serializer == GraphQLSerializer.JACKSON) {
                enumValueTypeSpecBuilder.addAnnotation(
                    AnnotationSpec.builder(JsonProperty::class.java)
                        .addMember("%S", enumValueDefinition.name)
                        .build()
                )
            } else {
                enumValueTypeSpecBuilder.addAnnotation(
                    AnnotationSpec.builder(SerialName::class.java)
                        .addMember("%S", enumValueDefinition.name)
                        .build()
                )
            }
        }
        enumTypeSpecBuilder.addEnumConstant(enumName, enumValueTypeSpecBuilder.build())
    }

    val unknownTypeSpec = TypeSpec.anonymousClassBuilder()
        .addKdoc("%L", "This is a default enum value that will be used when attempting to deserialize unknown value.")
    if (context.serializer == GraphQLSerializer.JACKSON) {
        unknownTypeSpec.addAnnotation(JsonEnumDefaultValue::class)
    } else {
        enumTypeSpecBuilder.addAnnotation(Serializable::class)
    }

    enumTypeSpecBuilder.addEnumConstant(UNKNOWN_VALUE, unknownTypeSpec.build())

    return enumTypeSpecBuilder.build()
}
