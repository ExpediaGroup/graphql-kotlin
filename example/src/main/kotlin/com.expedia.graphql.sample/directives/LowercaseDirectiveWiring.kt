package com.expedia.graphql.sample.directives

import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment

class LowercaseDirectiveWiring : SchemaDirectiveWiring {

    override fun onField(environment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher: DataFetcher<Any> = field.dataFetcher

        val lowerCaseFetcher = DataFetcher<String> { dataEnv ->
            originalDataFetcher.get(dataEnv).toString().toLowerCase()
        }
        return field.transform { it.dataFetcher(lowerCaseFetcher) }
    }
}