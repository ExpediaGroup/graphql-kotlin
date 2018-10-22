package com.expedia.graphql.schema.generator

import graphql.schema.DataFetcherFactory
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLOutputType

internal fun updatePropertyFieldBuilder(propertyType: GraphQLOutputType, fieldBuilder: GraphQLFieldDefinition.Builder, dataFetcherFactory: DataFetcherFactory<*>?): GraphQLFieldDefinition.Builder {
    val updatedFieldBuilder = if (propertyType is GraphQLNonNull) {
        val graphQLOutputType = propertyType.wrappedType as? GraphQLOutputType
        if (graphQLOutputType != null) fieldBuilder.type(graphQLOutputType) else fieldBuilder
    } else {
        fieldBuilder
    }

    return updatedFieldBuilder.dataFetcherFactory(dataFetcherFactory)
}
