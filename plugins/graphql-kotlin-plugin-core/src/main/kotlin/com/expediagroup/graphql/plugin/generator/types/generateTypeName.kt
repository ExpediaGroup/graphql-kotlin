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

internal fun generateCustomClassName(context: GraphQLClientGeneratorContext, graphQLType: NamedNode<*>, selectionSet: SelectionSet? = null): ClassName {
    val graphQLTypeDefinition: TypeDefinition<*> = context.graphQLSchema.getType(graphQLType.name).get()
    val graphQLTypeName = graphQLTypeDefinition.name
    val cachedTypeName = context.classNameCache[graphQLTypeName]
    return if (cachedTypeName == null) {
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
                else -> throw RuntimeException("Not supported")
            }
            ClassName(context.packageName, "${context.rootType}.${typeSpec.name}")
        }
        context.classNameCache[graphQLTypeName] = className
        className
    } else {
        validateCachedGraphQLType(context, graphQLTypeName, graphQLTypeDefinition, selectionSet)
        cachedTypeName
    }
}

private fun validateCachedGraphQLType(context: GraphQLClientGeneratorContext, graphQLTypeName: String, graphQLTypeDefinition: TypeDefinition<*>, selectionSet: SelectionSet?) {
    if (selectionSet != null) {
        // only need to verify objects and interfaces
        // unions don't have any common fields
        val selectedFields = when (graphQLTypeDefinition) {
            is ObjectTypeDefinition -> calculateSelectedFields(context, graphQLTypeName, selectionSet)
            is InterfaceTypeDefinition -> calculateSelectedFields(context, graphQLTypeName, selectionSet)
            else -> emptySet()
        }

        val typeSpec = context.typeSpecs[graphQLTypeName]
        val properties = typeSpec?.propertySpecs?.map { it.name }?.toSet() ?: emptySet()

        if (selectedFields.size != properties.size || selectedFields.minus(properties).isNotEmpty()) {
            throw RuntimeException("multiple selections of $graphQLTypeName GraphQL type with different selection sets")
        }
    }
}

private fun calculateSelectedFields(context: GraphQLClientGeneratorContext, targetType: String, selectionSet: SelectionSet): Set<String> {
    val result = mutableSetOf<String>()
    selectionSet.selections.forEach { selection ->
        when (selection) {
            is Field -> if ("__typename" != selection.name) {
                result.add(selection.name)
            }
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
