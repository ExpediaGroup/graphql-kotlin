package com.expediagroup.graphql.sample.directives

import com.expediagroup.graphql.directives.KotlinFieldDirectiveEnvironment
import com.expediagroup.graphql.directives.KotlinSchemaDirectiveWiring
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactories
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import java.util.function.BiFunction

class LowercaseSchemaDirectiveWiring : KotlinSchemaDirectiveWiring {

    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher: DataFetcher<Any> = environment.getDataFetcher()

        val lowerCaseFetcher = DataFetcherFactories.wrapDataFetcher(
            originalDataFetcher,
            BiFunction<DataFetchingEnvironment, Any, Any>{ _, value -> value.toString().toLowerCase() }
        )
        environment.setDataFetcher(lowerCaseFetcher)
        return field
    }
}
