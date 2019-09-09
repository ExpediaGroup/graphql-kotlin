/*
 * Copyright 2019 Expedia Group
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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.directives.deprecatedDirectiveWithReason
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getPropertyDeprecationReason
import com.expediagroup.graphql.generator.extensions.getPropertyDescription
import com.expediagroup.graphql.generator.extensions.getPropertyName
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.generator.extensions.isPropertyGraphQLID
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLOutputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class PropertyBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun property(prop: KProperty<*>, parentClass: KClass<*>): GraphQLFieldDefinition {
        val propertyType = graphQLTypeOf(type = prop.returnType, annotatedAsID = prop.isPropertyGraphQLID(parentClass))
            .safeCast<GraphQLOutputType>()

        val fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
            .description(prop.getPropertyDescription(parentClass))
            .name(prop.getPropertyName(parentClass))
            .type(propertyType)

        prop.getPropertyDeprecationReason(parentClass)?.let {
            fieldBuilder.deprecate(it)
            fieldBuilder.withDirective(deprecatedDirectiveWithReason(it))
        }

        generator.directives(prop, parentClass).forEach {
            fieldBuilder.withDirective(it)
        }

        val field = fieldBuilder.build()

        val parentType = parentClass.getSimpleName()
        val coordinates = FieldCoordinates.coordinates(parentType, prop.name)
        val dataFetcherFactory = config.dataFetcherFactoryProvider.propertyDataFetcherFactory(kClass = parentClass, kProperty = prop)
        generator.codeRegistry.dataFetcher(coordinates, dataFetcherFactory)

        return config.hooks.onRewireGraphQLType(field, coordinates, codeRegistry).safeCast()
    }
}
