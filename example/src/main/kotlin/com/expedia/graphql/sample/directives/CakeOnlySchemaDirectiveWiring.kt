package com.expedia.graphql.sample.directives

import com.expedia.graphql.directives.KotlinSchemaDirectiveEnvironment
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment

class CakeOnlySchemaDirectiveWiring : SchemaDirectiveWiring {

    @Throws(RuntimeException::class)
    override fun onField(environment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {
        val field = environment.element
        val coordinates = (environment as KotlinSchemaDirectiveEnvironment).coordinates
        val originalDataFetcher: DataFetcher<Any> = environment.codeRegistry.getDataFetcher(coordinates, field)

        val cakeOnlyFetcher = DataFetcher<Any> { dataEnv ->
            val strArg: String? = dataEnv.getArgument(environment.element.arguments[0].name) as String?
            if (!"cake".equals(other = strArg, ignoreCase = true)) {
                throw RuntimeException("The cake is a lie!")
            }
            originalDataFetcher.get(dataEnv)
        }
        environment.codeRegistry.dataFetcher(coordinates, cakeOnlyFetcher)
        return field
    }
}
