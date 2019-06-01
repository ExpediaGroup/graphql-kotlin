package com.expedia.graphql.sample.directives
import com.expedia.graphql.directives.KotlinSchemaDirectiveEnvironment
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactories
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import java.util.function.BiFunction

class LowercaseSchemaDirectiveWiring : SchemaDirectiveWiring {

    override fun onField(environment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {
        val field = environment.element
        val coordinates = (environment as KotlinSchemaDirectiveEnvironment).coordinates
        val originalDataFetcher: DataFetcher<Any> = environment.codeRegistry.getDataFetcher(coordinates, field)

        val lowerCaseFetcher = DataFetcherFactories.wrapDataFetcher(
            originalDataFetcher,
            BiFunction<DataFetchingEnvironment, Any, Any>{ _, value -> value.toString().toLowerCase() }
        )
        environment.codeRegistry.dataFetcher(coordinates, lowerCaseFetcher)
        return field
    }
}
