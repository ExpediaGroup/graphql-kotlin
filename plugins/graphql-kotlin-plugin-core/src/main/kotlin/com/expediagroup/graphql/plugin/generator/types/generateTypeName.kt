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
import graphql.language.FragmentDefinition
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
        else -> throw RuntimeException("Unsupported GraphQL type $graphQLType")
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
        val className = if (graphQLTypeDefinition is ScalarTypeDefinition && context.scalarTypeToConverterMapping[graphQLTypeName] == null) {
            val typeAlias = generateGraphQLCustomScalarTypeAlias(context, graphQLTypeDefinition)
            ClassName(context.packageName, typeAlias.name)
        } else {
            val typeSpec = when (graphQLTypeDefinition) {
                is ObjectTypeDefinition -> generateGraphQLObjectTypeSpec(context, graphQLTypeDefinition, selectionSet)
                is InputObjectTypeDefinition -> generateGraphQLInputObjectTypeSpec(context, graphQLTypeDefinition)
                is EnumTypeDefinition -> generateGraphQLEnumTypeSpec(context, graphQLTypeDefinition)
                is InterfaceTypeDefinition -> generateGraphQLInterfaceTypeSpec(context, graphQLTypeDefinition, selectionSet)
                is UnionTypeDefinition -> generateGraphQLUnionTypeSpec(context, graphQLTypeDefinition, selectionSet)
                is ScalarTypeDefinition -> generateGraphQLCustomScalarTypeSpec(context, graphQLTypeDefinition)
                else -> throw RuntimeException("should never happen")
            }
            ClassName(context.packageName, "${context.rootType}.${typeSpec.name}")
        }
        context.classNameCache[graphQLTypeName] = mutableListOf(className)
        className
    } else if (selectionSet == null) {
        cachedTypeNames.first()
    } else {
        // verify we got same selection set for interface and/or objects (unions shouldn't have any fields)
        for (cachedType in cachedTypeNames) {
            if (isCachedTypeApplicable(context, cachedType.simpleName, graphQLTypeDefinition, selectionSet)) {
                return cachedType
            }
        }

        // if different selection set we need to generate custom type
        val overriddenName = "$graphQLTypeName${cachedTypeNames.size + 1}"
        val typeSpec = when (graphQLTypeDefinition) {
            is ObjectTypeDefinition -> generateGraphQLObjectTypeSpec(context, graphQLTypeDefinition, selectionSet, overriddenName)
            is InterfaceTypeDefinition -> generateGraphQLInterfaceTypeSpec(context, graphQLTypeDefinition, selectionSet, overriddenName)
            is UnionTypeDefinition -> generateGraphQLUnionTypeSpec(context, graphQLTypeDefinition, selectionSet, overriddenName)
            else -> throw RuntimeException("should never happen")
        }
        val className = ClassName(context.packageName, "${context.rootType}.${typeSpec.name}")
        context.classNameCache[overriddenName]?.add(className)
        className
    }
}

private fun isCachedTypeApplicable(context: GraphQLClientGeneratorContext, graphQLTypeName: String, graphQLTypeDefinition: TypeDefinition<*>, selectionSet: SelectionSet): Boolean =
    when (graphQLTypeDefinition) {
        is UnionTypeDefinition -> {
            val unionImplementations = graphQLTypeDefinition.memberTypes.filterIsInstance(graphql.language.TypeName::class.java).map { it.name }
            var result = true
            for (unionImplementation in unionImplementations) {
                result = result && verifySelectionSet(context, unionImplementation, selectionSet)
                if (!result) {
                    break
                }
            }
            result
        }
        is InterfaceTypeDefinition -> {
            var result = verifySelectionSet(context, graphQLTypeName, selectionSet)
            if (result) {
                val implementations = context.graphQLSchema.getImplementationsOf(graphQLTypeDefinition).map { it.name }
                for (implementation in implementations) {
                    result = result && verifySelectionSet(context, implementation, selectionSet)
                    if (!result) {
                        break
                    }
                }
            }
            result
        }
        is ObjectTypeDefinition -> verifySelectionSet(context, graphQLTypeName, selectionSet)
        else -> true
    }

private fun verifySelectionSet(context: GraphQLClientGeneratorContext, graphQLTypeName: String, selectionSet: SelectionSet): Boolean {
    val selectedFields = calculateSelectedFields(context, graphQLTypeName, selectionSet)
    val typeSpec = context.typeSpecs[graphQLTypeName]
    val properties = typeSpec?.propertySpecs?.map { it.name }?.toSet() ?: emptySet()
    return selectedFields == properties ||
        (selectedFields.minus(properties).size == 1 && selectedFields.contains("__typename") && context.objectsWithTypeNameSelection.contains(graphQLTypeName))
}

private fun calculateSelectedFields(
    context: GraphQLClientGeneratorContext,
    targetType: String,
    selectionSet: SelectionSet
): Set<String> {
    val result = mutableSetOf<String>()
    selectionSet.selections.forEach { selection ->
        when (selection) {
            is Field -> result.add(selection.name)
            is InlineFragment -> if (selection.typeCondition.name == targetType) {
                result.addAll(calculateSelectedFields(context, targetType, selection.selectionSet))
            }
            is FragmentSpread -> {
                val fragmentDefinition = context.queryDocument
                    .getDefinitionsOfType(FragmentDefinition::class.java)
                    .find { it.name == selection.name } ?: throw RuntimeException("fragment not found")
                if (fragmentDefinition.typeCondition.name == targetType) {
                    result.addAll(calculateSelectedFields(context, targetType, fragmentDefinition.selectionSet))
                }
            }
        }
    }
    return result
}
