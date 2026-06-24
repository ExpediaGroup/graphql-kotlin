/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.annotations.GraphQLOneOfFieldType
import com.expediagroup.graphql.generator.directives.deprecatedDirectiveWithReason
import com.expediagroup.graphql.generator.internal.extensions.getDeprecationReason
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.internal.types.utils.getValidOneOfUnwrappedFieldParameter
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputType
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability

/*
 * WRAPPED subtype directives are ambiguous: @SimpleDirective on ParagraphInput could target
 * `input ParagraphInput @simpleDirective` or
 * `paragraph: ParagraphInput @simpleDirective`.
 * Add an explicit way to distinguish type directives from synthetic oneOf field directives.
 */
internal fun generateOneOfInputProperty(
    generator: SchemaGenerator,
    metadata: OneOfFieldMetadata
): GraphQLInputObjectField {
    // Field metadata comes from the generated field source: constructor parameter for UNWRAPPED, subtype class for WRAPPED.
    val unwrappedParameter = if (metadata.fieldType == GraphQLOneOfFieldType.UNWRAPPED) {
        getValidOneOfUnwrappedFieldParameter(metadata.subClass)
    } else {
        null
    }
    val fieldMetadata: KAnnotatedElement = unwrappedParameter ?: metadata.subClass
    // oneOf input fields stay nullable, @oneOf validation enforces that exactly one nullable field is provided.
    val type = unwrappedParameter?.type?.withNullability(true) ?: metadata.subClass.createType(nullable = true)
    val graphQLInputType = generateGraphQLType(
        generator = generator,
        type = type,
        typeInfo = GraphQLKTypeMetadata(inputType = true, fieldName = metadata.fieldName, fieldAnnotations = unwrappedParameter?.annotations.orEmpty()),
    ).safeCast<GraphQLInputType>()

    val builder = GraphQLInputObjectField.newInputObjectField()
        .name(metadata.fieldName)
        .description(fieldMetadata.getGraphQLDescription())
        .type(graphQLInputType)

    fieldMetadata.getDeprecationReason()?.let {
        builder.deprecate(it)
        builder.withAppliedDirective(deprecatedDirectiveWithReason(it))
    }

    unwrappedParameter?.let {
        generateDirectives(generator, it, DirectiveLocation.INPUT_FIELD_DEFINITION).forEach { directive ->
            builder.withAppliedDirective(directive)
        }
    }

    return builder.build()
}
