package com.expedia.graphql.sample.directives

import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiringEnvironment

class CakeOnlyDirectiveWiring : DirectiveWiring {

    override fun isApplicable(environment: SchemaDirectiveWiringEnvironment<*>): Boolean =
            environment.directive.name == getDirectiveName(CakeOnly::class)


    override fun onField(wiringEnv: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {
        val field = wiringEnv.element
        val originalDataFetcher: DataFetcher<Any> = field.dataFetcher
        val cakeOnlyFetcher = DataFetcher<Any> { dataEnv ->
            val strArg: String? = dataEnv.getArgument(wiringEnv.element.arguments[0].name) as String?
            if (strArg != "Cake") {
                throw RuntimeException("The cake is a lie!")
            }
            originalDataFetcher.get(dataEnv)
        }
        return field.transform { it.dataFetcher(cakeOnlyFetcher) }
    }
}