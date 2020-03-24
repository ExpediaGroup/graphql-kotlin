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

internal fun generateTypeName(context: GraphQLClientGeneratorContext, graphQLType: Type<*>, selectionSet: SelectionSet? = null): TypeName {
    val nullable = graphQLType !is NonNullType

    return when (graphQLType) {
        is NonNullType -> generateTypeName(context, graphQLType.type, selectionSet)
        is NamedNode<*> -> when (graphQLType.name) {
            Scalars.GraphQLString.name -> STRING
            Scalars.GraphQLInt.name -> INT
            Scalars.GraphQLFloat.name -> FLOAT
            Scalars.GraphQLBoolean.name -> BOOLEAN
            Scalars.GraphQLID.name -> ClassName(context.packageName, "ID")
            else -> generateCustomClassName(context, graphQLType, selectionSet)
        }
        is ListType -> LIST.parameterizedBy(generateTypeName(context, graphQLType.type, selectionSet))
        else -> throw RuntimeException("Unsupported GraphQL type $graphQLType")
    }.copy(nullable = nullable)
}

internal fun generateCustomClassName(context: GraphQLClientGeneratorContext, graphQLType: NamedNode<*>, selectionSet: SelectionSet? = null): ClassName {
    val graphQLTypeDefinition: TypeDefinition<*> = context.graphQLSchema.getType(graphQLType.name).get()
    val typeNameCacheKey = graphQLTypeDefinition.name
    val cachedTypeName = context.classNameCache[typeNameCacheKey]
    return if (cachedTypeName == null) {
        val typeSpec = when (graphQLTypeDefinition) {
            is ObjectTypeDefinition -> generateGraphQLObjectTypeSpec(context, graphQLTypeDefinition, selectionSet)
            is InputObjectTypeDefinition -> generateGraphQLInputObjectTypeSpec(context, graphQLTypeDefinition)
            is EnumTypeDefinition -> generateGraphQLEnumTypeSpec(context, graphQLTypeDefinition)
            is InterfaceTypeDefinition -> generateGraphQLInterfaceTypeSpec(context, graphQLTypeDefinition, selectionSet)
            is UnionTypeDefinition -> generateGraphQLUnionTypeSpec(context, graphQLTypeDefinition, selectionSet)
            is ScalarTypeDefinition -> generateGraphQLCustomScalarTypeSpec(context, graphQLTypeDefinition)
            else -> throw RuntimeException("Not supported")
        }

        val className = ClassName(context.packageName, "${context.rootType}.${typeSpec.name}")
        context.classNameCache[typeNameCacheKey] = className
        className
    } else {
        // TODO validate same selection set was used - fail on mismatch
        cachedTypeName
    }
}
