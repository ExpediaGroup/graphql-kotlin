package com.expedia.graphql.sample.directives

import com.expedia.graphql.directives.KotlinFieldDirectiveEnvironment
import com.expedia.graphql.directives.KotlinSchemaDirectiveWiring
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition

class CakeOnlySchemaDirectiveWiring : KotlinSchemaDirectiveWiring {

    @Throws(RuntimeException::class)
    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher: DataFetcher<Any> = environment.getDataFetcher()

        val cakeOnlyFetcher = DataFetcher<Any> { dataEnv ->
            val strArg: String? = dataEnv.getArgument(environment.element.arguments[0].name) as String?
            if (!"cake".equals(other = strArg, ignoreCase = true)) {
                throw RuntimeException("The cake is a lie!")
            }
            originalDataFetcher.get(dataEnv)
        }
        environment.setDataFetcher(cakeOnlyFetcher)
        return field
    }
}
