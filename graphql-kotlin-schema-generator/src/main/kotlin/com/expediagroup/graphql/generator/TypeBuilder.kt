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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.extensions.getKClass
import com.expediagroup.graphql.generator.extensions.isEnum
import com.expediagroup.graphql.generator.extensions.isInterface
import com.expediagroup.graphql.generator.extensions.isListType
import com.expediagroup.graphql.generator.extensions.isUnion
import com.expediagroup.graphql.generator.extensions.wrapInNonNull
import com.expediagroup.graphql.generator.state.KGraphQLType
import com.expediagroup.graphql.generator.state.SchemaGeneratorState
import com.expediagroup.graphql.generator.state.TypesCacheKey
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLTypeUtil
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal open class TypeBuilder constructor(protected val generator: SchemaGenerator) {
    protected val state: SchemaGeneratorState = generator.state
    protected val config: SchemaGeneratorConfig = generator.config
    protected val subTypeMapper: SubTypeMapper = generator.subTypeMapper
    protected val codeRegistry: GraphQLCodeRegistry.Builder = generator.codeRegistry

    internal fun graphQLTypeOf(type: KType, inputType: Boolean = false, annotatedAsID: Boolean = false): GraphQLType {
        val hookGraphQLType = config.hooks.willGenerateGraphQLType(type)
        val graphQLType = hookGraphQLType
            ?: generator.scalarType(type, annotatedAsID)
            ?: objectFromReflection(type, inputType)

        // Do not call the hook on GraphQLTypeReference as we have not generated the type yet
        val unwrappedType = GraphQLTypeUtil.unwrapType(graphQLType).lastElement()
        if (unwrappedType !is GraphQLTypeReference) {
            val typeWithNullability = graphQLType.wrapInNonNull(type)
            return config.hooks.didGenerateGraphQLType(type, typeWithNullability)
        }

        return graphQLType
    }

    private fun objectFromReflection(type: KType, inputType: Boolean): GraphQLType {
        val cacheKey = TypesCacheKey(type, inputType)
        val cachedType = state.cache.get(cacheKey)

        if (cachedType != null) {
            return cachedType
        }

        val kClass = type.getKClass()
        val graphQLType = getGraphQLType(kClass, inputType, type)

        val modifiedGraphQLType = config.hooks.willAddGraphQLTypeToSchema(type, graphQLType)

        if (modifiedGraphQLType !is GraphQLTypeReference) {
            val kGraphQLType = KGraphQLType(kClass, modifiedGraphQLType)
            state.cache.put(cacheKey, kGraphQLType)
        }

        return modifiedGraphQLType
    }

    private fun getGraphQLType(kClass: KClass<*>, inputType: Boolean, type: KType): GraphQLType = when {
        kClass.isEnum() -> @Suppress("UNCHECKED_CAST") (generator.enumType(kClass as KClass<Enum<*>>))
        kClass.isListType() -> generator.listType(type, inputType)
        kClass.isUnion() -> generator.unionType(kClass)
        kClass.isInterface() -> generator.interfaceType(kClass)
        inputType -> generator.inputObjectType(kClass)
        else -> generator.objectType(kClass)
    }
}
