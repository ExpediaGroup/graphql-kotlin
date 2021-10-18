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
import com.squareup.kotlinpoet.TypeAliasSpec
import graphql.language.ScalarTypeDefinition

/**
 * Generate String type alias to custom GraphQL scalars (including ID) since they are serialized as Strings by default.
 */
internal fun generateGraphQLCustomScalarTypeAlias(context: GraphQLClientGeneratorContext, scalarTypeDefinition: ScalarTypeDefinition): TypeAliasSpec {
    val typeAliasSpec = TypeAliasSpec.builder(scalarTypeDefinition.name, String::class)
    scalarTypeDefinition.description?.content?.let { kdoc ->
        typeAliasSpec.addKdoc("%L", kdoc)
    }

    val typeAlias = typeAliasSpec.build()
    context.typeAliases[scalarTypeDefinition.name] = typeAlias
    return typeAlias
}
