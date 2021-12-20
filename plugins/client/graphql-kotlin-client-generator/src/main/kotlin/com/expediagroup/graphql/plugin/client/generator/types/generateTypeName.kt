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
import com.expediagroup.graphql.plugin.client.generator.ScalarConverterInfo
import com.expediagroup.graphql.plugin.client.generator.exceptions.UnknownGraphQLTypeException
import com.expediagroup.graphql.plugin.client.generator.extensions.findFragmentDefinition
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
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
import kotlinx.serialization.Serializable

/**
 * Generate [TypeName] reference to a Kotlin class representation of an underlying GraphQL type.
 */
internal fun generateTypeName(
    context: GraphQLClientGeneratorContext,
    graphQLType: Type<*>,
    selectionSet: SelectionSet? = null,
    optional: Boolean = false
): TypeName {
    val nullable = optional || graphQLType !is NonNullType

    return when (graphQLType) {
        is NonNullType -> generateTypeName(context, graphQLType.type, selectionSet)
        is NamedNode<*> -> when (graphQLType.name) {
            Scalars.GraphQLString.name -> STRING
            Scalars.GraphQLInt.name -> INT
            Scalars.GraphQLFloat.name -> DOUBLE
            Scalars.GraphQLBoolean.name -> BOOLEAN
            else -> generateCustomClassName(context, graphQLType, selectionSet)
        }
        is ListType -> {
            val type = generateTypeName(context, graphQLType.type, selectionSet)
            val parameterizedType = if (context.serializer == GraphQLSerializer.KOTLINX && context.isCustomScalar(type)) {
                val (serializerClassName, _) = context.scalarClassToConverterTypeSpecs[type] as ScalarConverterInfo.KotlinxSerializerInfo
                type.copy(
                    annotations = listOf(
                        AnnotationSpec.builder(Serializable::class)
                            .addMember("with = %T::class", serializerClassName)
                            .build()
                    )
                )
            } else {
                type
            }
            LIST.parameterizedBy(parameterizedType)
        }
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
            lateinit var className: ClassName
            // generate corresponding type spec
            when (graphQLTypeDefinition) {
                is ObjectTypeDefinition -> {
                    className = generateClassName(context, graphQLTypeDefinition, selectionSet)
                    context.typeSpecs[className] = generateGraphQLObjectTypeSpec(context, graphQLTypeDefinition, selectionSet)
                }
                is InputObjectTypeDefinition -> {
                    className = generateClassName(context, graphQLTypeDefinition, selectionSet, packageName = "${context.packageName}.inputs")
                    context.inputClassToTypeSpecs[className] = generateGraphQLInputObjectTypeSpec(context, graphQLTypeDefinition)
                }
                is EnumTypeDefinition -> {
                    className = generateClassName(context, graphQLTypeDefinition, selectionSet, packageName = "${context.packageName}.enums")
                    context.enumClassToTypeSpecs[className] = generateGraphQLEnumTypeSpec(context, graphQLTypeDefinition)
                }
                is InterfaceTypeDefinition -> {
                    className = generateClassName(context, graphQLTypeDefinition, selectionSet)
                    context.polymorphicTypes[className] = mutableListOf(className)
                    context.typeSpecs[className] = generateGraphQLInterfaceTypeSpec(context, graphQLTypeDefinition, selectionSet)
                }
                is UnionTypeDefinition -> {
                    className = generateClassName(context, graphQLTypeDefinition, selectionSet)
                    context.polymorphicTypes[className] = mutableListOf(className)
                    context.typeSpecs[className] = generateGraphQLUnionTypeSpec(context, graphQLTypeDefinition, selectionSet)
                }
                is ScalarTypeDefinition -> {
                    // its not possible to enter this clause if converter is not available
                    val graphQLScalarMapping = context.customScalarMap[graphQLTypeName]!!
                    className = graphQLScalarMapping.className
                    context.classNameCache[graphQLTypeName] = mutableListOf(className)

                    val converterInfo = generateGraphQLCustomScalarConverters(context, className, graphQLScalarMapping.converterClassName)
                    context.scalarClassToConverterTypeSpecs[className] = converterInfo
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
        val typeSpec = when (graphQLTypeDefinition) {
            is ObjectTypeDefinition -> generateGraphQLObjectTypeSpec(context, graphQLTypeDefinition, selectionSet, overriddenName)
            is InterfaceTypeDefinition -> {
                context.polymorphicTypes[className] = mutableListOf(className)
                generateGraphQLInterfaceTypeSpec(context, graphQLTypeDefinition, selectionSet, overriddenName)
            }
            is UnionTypeDefinition -> {
                context.polymorphicTypes[className] = mutableListOf(className)
                generateGraphQLUnionTypeSpec(context, graphQLTypeDefinition, selectionSet, overriddenName)
            }
            // should never happen as we can only generate different object, interface or union type
            else -> throw UnknownGraphQLTypeException(graphQLType)
        }
        context.typeSpecs[className] = typeSpec
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
    nameOverride: String? = null,
    packageName: String = "${context.packageName}.${context.operationName.lowercase()}"
): ClassName {
    val typeName = nameOverride ?: graphQLType.name
    val className = ClassName(packageName, typeName)
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
