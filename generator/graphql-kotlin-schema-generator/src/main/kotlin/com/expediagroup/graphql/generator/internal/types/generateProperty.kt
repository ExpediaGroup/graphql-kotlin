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
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLOutputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal fun generateProperty(generator: SchemaGenerator, prop: KProperty<*>, parentClass: KClass<*>): GraphQLFieldDefinition {
    val typeFromHooks = generator.config.hooks.willResolveMonad(prop.returnType)
    val propertyName = prop.getPropertyName(parentClass)
    val typeInfo = GraphQLKTypeMetadata(fieldName = propertyName, fieldAnnotations = prop.getPropertyAnnotations(parentClass))
    val propertyType = generateGraphQLType(generator, type = typeFromHooks, typeInfo).safeCast<GraphQLOutputType>()

    val fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
        .description(prop.getPropertyDescription(parentClass))
        .name(propertyName)
        .type(propertyType)

    prop.getPropertyDeprecationReason(parentClass)?.let {
        fieldBuilder.deprecate(it)
        fieldBuilder.withAppliedDirective(deprecatedDirectiveWithReason(it))
    }

    generateDirectives(generator, prop, DirectiveLocation.FIELD_DEFINITION, parentClass).forEach {
        fieldBuilder.withAppliedDirective(it)
    }

    val field = fieldBuilder.build()

    val parentType = parentClass.getSimpleName()
    val coordinates = FieldCoordinates.coordinates(parentType, propertyName)
    val dataFetcherFactory = generator.config.dataFetcherFactoryProvider.propertyDataFetcherFactory(kClass = parentClass, kProperty = prop)
    generator.codeRegistry.dataFetcher(coordinates, dataFetcherFactory)

    return generator.config.hooks.onRewireGraphQLType(field, coordinates, generator.codeRegistry).safeCast()
}
