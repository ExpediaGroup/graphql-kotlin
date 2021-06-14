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

import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.client.generator.exceptions.InvalidSelectionSetException
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.InterfaceTypeDefinition
import graphql.language.SelectionSet

/**
 * Generate interface [TypeSpec] from the GraphQL interface definition based on the specified selection set.
 *
 * @see generateGraphQLUnionTypeSpec
 * @see generateInterfaceTypeSpec
 */
internal fun generateGraphQLInterfaceTypeSpec(
    context: GraphQLClientGeneratorContext,
    interfaceDefinition: InterfaceTypeDefinition,
    selectionSet: SelectionSet?,
    interfaceNameOverride: String? = null
): TypeSpec {
    if (selectionSet == null || selectionSet.selections.isEmpty()) {
        throw InvalidSelectionSetException(context.operationName, interfaceDefinition.name, "interface")
    }

    val interfaceName = interfaceNameOverride ?: interfaceDefinition.name
    val implementations = context.graphQLSchema.getImplementationsOf(interfaceDefinition).map { it.name }

    return generateInterfaceTypeSpec(
        context = context,
        interfaceName = interfaceName,
        kdoc = interfaceDefinition.description?.content,
        fields = interfaceDefinition.fieldDefinitions,
        selectionSet = selectionSet,
        implementations = implementations
    )
}
