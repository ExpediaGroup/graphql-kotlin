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

internal fun generateKotlinTypeName(context: GraphQLClientGeneratorContext, graphQLType: Type<*>, selectionSet: SelectionSet? = null): TypeName {
    val nullable = graphQLType !is NonNullType

    return when (graphQLType) {
        is NonNullType -> generateKotlinTypeName(context, graphQLType.type, selectionSet)
        is NamedNode<*> -> when (graphQLType.name) {
            Scalars.GraphQLString.name -> STRING
            Scalars.GraphQLInt.name -> INT
            Scalars.GraphQLFloat.name -> FLOAT
            Scalars.GraphQLBoolean.name -> BOOLEAN
            Scalars.GraphQLID.name -> ClassName(context.packageName, "ID")
            else -> generateCustomKotlinTypeName(context, graphQLType, selectionSet)
        }
        is ListType -> LIST.parameterizedBy(generateKotlinTypeName(context, graphQLType.type, selectionSet))
        else -> throw RuntimeException("Unsupported GraphQL type $graphQLType")
    }.copy(nullable = nullable)
}

internal fun generateCustomKotlinTypeName(context: GraphQLClientGeneratorContext, graphQLType: NamedNode<*>, selectionSet: SelectionSet? = null): TypeName {
    val graphQLTypeDefinition: TypeDefinition<*> = context.graphQLSchema.getType(graphQLType.name).get()
    // TODO update typeNameCacheKey to account for different selection sets
    //    val typeNameCacheKey = when (graphQLTypeDefinition) {
    //        is ObjectTypeDefinition -> graphQLTypeDefinition.name + selectionSet?.getSelectionsOfType(Field::class.java)?.joinToString { it.name }
    //        is InputObjectTypeDefinition -> graphQLTypeDefinition.name
    //        is EnumTypeDefinition -> graphQLTypeDefinition.name
    //        else -> RuntimeException("unsupported")
    //    }
    val typeNameCacheKey = graphQLTypeDefinition.name
    val cachedTypeName = context.typeNameCache[typeNameCacheKey]
    return if (cachedTypeName == null) {
        val typeSpec = when (graphQLTypeDefinition) {
            is ObjectTypeDefinition -> if (selectionSet == null) {
                throw RuntimeException("cannot select empty objects")
            } else {
                generateObjectTypeSpec(context, graphQLTypeDefinition, selectionSet)
            }
            is InputObjectTypeDefinition -> generateInputObjectTypeSpec(context, graphQLTypeDefinition)
            is EnumTypeDefinition -> generateEnumTypeSpec(context, graphQLTypeDefinition)
            is InterfaceTypeDefinition -> if (selectionSet == null) {
                throw RuntimeException("cannot select empty interface")
            } else {
                generateInterfaceTypeSpec(context, graphQLTypeDefinition, selectionSet)
            }
            is UnionTypeDefinition -> if (selectionSet == null) {
                throw RuntimeException("cannot select empty interface")
            } else {
                generateUnionTypeSpec(context, graphQLTypeDefinition, selectionSet)
            }
            is ScalarTypeDefinition -> generateCustomScalarTypeSpec(context, graphQLTypeDefinition)
            else -> throw RuntimeException("Not supported")
        }

        val typeName = ClassName(context.packageName, "${context.rootType}.${typeSpec.name!!}")
        context.typeNameCache[typeNameCacheKey] = typeName
        typeName
    } else {
        cachedTypeName
    }
}
