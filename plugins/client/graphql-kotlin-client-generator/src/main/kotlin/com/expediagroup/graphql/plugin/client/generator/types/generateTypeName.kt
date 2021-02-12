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
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import com.expediagroup.graphql.plugin.client.generator.exceptions.UnknownGraphQLTypeException
import com.expediagroup.graphql.plugin.client.generator.extensions.findFragmentDefinition
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import graphql.Scalars
import graphql.language.EnumTypeDefinition
import graphql.language.Field
import graphql.language.FragmentSpread
import graphql.language.InlineFragment
import graphql.language.InputObjectTypeDefinition
import graphql.language.InterfaceTypeDefinition
import graphql.language.ListType
import graphql.language.NamedNode
import graphql.language.NonNullType
import graphql.language.ObjectTypeDefinition
import graphql.language.ScalarTypeDefinition
import graphql.language.SelectionSet
import graphql.language.Type
import graphql.language.TypeDefinition
import graphql.language.UnionTypeDefinition

/**
 * Generate [TypeName] reference to a Kotlin class representation of an underlying GraphQL type.
 */
internal fun generateTypeName(context: GraphQLClientGeneratorContext, graphQLType: Type<*>, selectionSet: SelectionSet? = null): TypeName {
    val nullable = graphQLType !is NonNullType

    return when (graphQLType) {
        is NonNullType -> generateTypeName(context, graphQLType.type, selectionSet)
        is NamedNode<*> -> when (graphQLType.name) {
            Scalars.GraphQLString.name -> STRING
            Scalars.GraphQLInt.name -> INT
            Scalars.GraphQLFloat.name -> FLOAT
            Scalars.GraphQLBoolean.name -> BOOLEAN
            else -> generateCustomClassName(context, graphQLType, selectionSet)
        }
        is ListType -> LIST.parameterizedBy(generateTypeName(context, graphQLType.type, selectionSet))
        // should never happen
        else -> throw UnknownGraphQLTypeException(graphQLType)
    }.copy(nullable = nullable)
}

/**
 * Generate custom [ClassName] reference to a Kotlin class representing GraphQL complex type (object, input object, enum, interface, union or custom scalar). If class name was not yet generated, this
 * method will generate the underlying type spec definition as well. If class name was retrieved from the cache we re-validate interface and object types to ensure they were generated with the same
 * selection set.
 */
internal fun generateCustomClassName(context: GraphQLClientGeneratorContext, graphQLType: NamedNode<*>, selectionSet: SelectionSet? = null): ClassName {
    val graphQLTypeDefinition: TypeDefinition<*> = context.graphQLSchema.getType(graphQLType.name).get()
    val graphQLTypeName = graphQLTypeDefinition.name
    val cachedTypeNames = context.classNameCache[graphQLTypeName]

    return if (cachedTypeNames == null || cachedTypeNames.isEmpty()) {
        // build new custom type
        if (graphQLTypeDefinition is ScalarTypeDefinition && context.customScalarMap[graphQLTypeName] == null) {
            val typeAlias = generateGraphQLCustomScalarTypeAlias(context, graphQLTypeDefinition)
            val className = ClassName(context.packageName, typeAlias.name)
            context.classNameCache[graphQLTypeName] = mutableListOf(className)
            className
        } else {
            val className = generateClassName(context, graphQLTypeDefinition, selectionSet)
            // generate corresponding type spec
            when (graphQLTypeDefinition) {
                is ObjectTypeDefinition -> generateGraphQLObjectTypeSpec(context, graphQLTypeDefinition, selectionSet)
                is InputObjectTypeDefinition -> generateGraphQLInputObjectTypeSpec(context, graphQLTypeDefinition)
                is EnumTypeDefinition -> {
                    generateGraphQLEnumTypeSpec(context, graphQLTypeDefinition)
                    context.enums.add(className)
                }
                is InterfaceTypeDefinition -> generateGraphQLInterfaceTypeSpec(context, graphQLTypeDefinition, selectionSet)
                is UnionTypeDefinition -> generateGraphQLUnionTypeSpec(context, graphQLTypeDefinition, selectionSet)
                is ScalarTypeDefinition -> {
                    // its not possible to enter this clause if converter is not available
                    val graphQLScalarMapping = context.customScalarMap[graphQLTypeName]!!
                    generateGraphQLCustomScalarTypeSpec(context, graphQLTypeDefinition, graphQLScalarMapping)
                    if (context.serializer == GraphQLSerializer.KOTLINX) {
                        generateGraphQLCustomScalarKSerializer(context, graphQLTypeDefinition, graphQLScalarMapping.converter, className)
                    }
                }
                // should never happen as above list covers all graphql types
                else -> throw UnknownGraphQLTypeException(graphQLType)
            }
            className
        }
    } else if (selectionSet == null) {
        cachedTypeNames.first()
    } else {
        // verify we got same selection set for interface and/or objects (unions shouldn't have any fields)
        for (cachedType in cachedTypeNames) {
            if (isCachedTypeApplicable(context, cachedType.simpleNameWithoutWrapper(), graphQLTypeDefinition, selectionSet)) {
                return cachedType
            }
        }

        // if different selection set we need to generate custom type
        val overriddenName = "$graphQLTypeName${cachedTypeNames.size + 1}"
        val className = generateClassName(context, graphQLTypeDefinition, selectionSet, overriddenName)

        // generate new type spec
        when (graphQLTypeDefinition) {
            is ObjectTypeDefinition -> generateGraphQLObjectTypeSpec(context, graphQLTypeDefinition, selectionSet, overriddenName)
            is InterfaceTypeDefinition -> generateGraphQLInterfaceTypeSpec(context, graphQLTypeDefinition, selectionSet, overriddenName)
            is UnionTypeDefinition -> generateGraphQLUnionTypeSpec(context, graphQLTypeDefinition, selectionSet, overriddenName)
            // should never happen as we can only generate different object, interface or union type
            else -> throw UnknownGraphQLTypeException(graphQLType)
        }
        className
    }
}

/**
 * Generate custom [ClassName] reference to a Kotlin class representing GraphQL complex type (object, input object, enum, interface, union or custom scalar) and caches the value.
 */
internal fun generateClassName(
    context: GraphQLClientGeneratorContext,
    graphQLType: NamedNode<*>,
    selectionSet: SelectionSet? = null,
    nameOverride: String? = null
): ClassName {
    val typeName = nameOverride ?: graphQLType.name
    val className = ClassName(context.packageName, "${context.rootType}.$typeName")
    val classNames = context.classNameCache.getOrDefault(graphQLType.name, mutableListOf())
    classNames.add(className)
    context.classNameCache[graphQLType.name] = classNames

    if (selectionSet != null) {
        val selectedFields = calculateSelectedFields(context, typeName, selectionSet)
        context.typeToSelectionSetMap[typeName] = selectedFields
    }

    return className
}

private fun ClassName.simpleNameWithoutWrapper() = this.simpleName.substringAfter(".")

private fun isCachedTypeApplicable(context: GraphQLClientGeneratorContext, graphQLTypeName: String, graphQLTypeDefinition: TypeDefinition<*>, selectionSet: SelectionSet): Boolean =
    when (graphQLTypeDefinition) {
        is UnionTypeDefinition -> verifySelectionSet(context, graphQLTypeName, selectionSet)
        is InterfaceTypeDefinition -> verifySelectionSet(context, graphQLTypeName, selectionSet)
        is ObjectTypeDefinition -> verifySelectionSet(context, graphQLTypeName, selectionSet)
        else -> true
    }

private fun verifySelectionSet(context: GraphQLClientGeneratorContext, graphQLTypeName: String, selectionSet: SelectionSet): Boolean {
    val selectedFields = calculateSelectedFields(context, graphQLTypeName, selectionSet)
    val cachedTypeFields = context.typeToSelectionSetMap[graphQLTypeName]
    return selectedFields == cachedTypeFields
}

private fun calculateSelectedFields(
    context: GraphQLClientGeneratorContext,
    targetType: String,
    selectionSet: SelectionSet,
    path: String = ""
): Set<String> {
    val result = mutableSetOf<String>()
    selectionSet.selections.forEach { selection ->
        when (selection) {
            is Field -> {
                result.add(path + selection.name)
                if (selection.selectionSet != null) {
                    result.addAll(calculateSelectedFields(context, targetType, selection.selectionSet, "$path${selection.name}."))
                }
            }
            is InlineFragment -> {
                val targetFragmentType = selection.typeCondition.name
                val fragmentPathPrefix = if (targetFragmentType == targetType) {
                    path
                } else {
                    "$path$targetFragmentType."
                }
                result.addAll(calculateSelectedFields(context, targetType, selection.selectionSet, fragmentPathPrefix))
            }
            is FragmentSpread -> {
                val fragmentDefinition = context.queryDocument.findFragmentDefinition(context, selection.name, targetType)
                val targetFragmentType = fragmentDefinition.typeCondition.name
                val fragmentPathPrefix = if (targetFragmentType == targetType) {
                    path
                } else {
                    "$path$targetFragmentType."
                }
                result.addAll(calculateSelectedFields(context, targetType, fragmentDefinition.selectionSet, fragmentPathPrefix))
            }
        }
    }
    return result
}
