/*
 * Copyright 2022 Expedia, Inc
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
import com.expediagroup.graphql.generator.directives.deprecatedDirectiveWithReason
import com.expediagroup.graphql.generator.internal.extensions.getPropertyAnnotations
import com.expediagroup.graphql.generator.internal.extensions.getPropertyDeprecationReason
import com.expediagroup.graphql.generator.internal.extensions.getPropertyDescription
import com.expediagroup.graphql.generator.internal.extensions.getPropertyName
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.internal.extensions.unwrapOptionalInputType
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal fun generateInputProperty(generator: SchemaGenerator, prop: KProperty<*>, parentClass: KClass<*>): GraphQLInputObjectField {
    // Verify that the unwrapped GraphQL type is a valid input type
    val inputTypeFromHooks = generator.config.hooks.willResolveInputMonad(prop.returnType)
    val unwrappedType = inputTypeFromHooks.unwrapOptionalInputType()
    val propertyName = prop.getPropertyName(parentClass)
    val typeInfo = GraphQLKTypeMetadata(inputType = true, fieldName = propertyName, fieldAnnotations = prop.getPropertyAnnotations(parentClass))
    val graphQLInputType = generateGraphQLType(generator = generator, type = unwrappedType, typeInfo).safeCast<GraphQLInputType>()

    val builder = GraphQLInputObjectField.newInputObjectField()
        .name(propertyName)
        .description(prop.getPropertyDescription(parentClass))
        .type(graphQLInputType)

    prop.getPropertyDeprecationReason(parentClass)?.let {
        builder.deprecate(it)
        builder.withAppliedDirective(deprecatedDirectiveWithReason(it))
    }

    generateDirectives(generator, prop, DirectiveLocation.INPUT_FIELD_DEFINITION, parentClass).forEach {
        builder.withAppliedDirective(it)
    }

    return generator.config.hooks.onRewireGraphQLType(builder.build(), null, generator.codeRegistry).safeCast()
}
