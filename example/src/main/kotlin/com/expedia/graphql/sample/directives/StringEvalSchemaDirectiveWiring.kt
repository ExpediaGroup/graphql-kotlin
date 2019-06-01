package com.expedia.graphql.sample.directives

import com.expedia.graphql.directives.KotlinSchemaDirectiveEnvironment
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment

class StringEvalSchemaDirectiveWiring : SchemaDirectiveWiring {
    private val directiveName = getDirectiveName(StringEval::class)

    override fun onField(environment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {
        val field = environment.element
        val coordinates = (environment as KotlinSchemaDirectiveEnvironment).coordinates
        val originalDataFetcher: DataFetcher<Any> = environment.codeRegistry.getDataFetcher(coordinates, field)

        val defaultValueFetcher = DataFetcher<Any> { dataEnv ->
            val newArguments = HashMap(dataEnv.arguments)
            environment.element.arguments.associate {
                Pair(it, dataEnv.getArgument(it.name) as String?)
            }.forEach { (graphQLArgumentType, value) ->
                if (graphQLArgumentType.getDirective(directiveName).getArgument(StringEval::lowerCase.name).value as Boolean) {
                    newArguments[graphQLArgumentType.name] = value?.toLowerCase()
                }
                if (value.isNullOrEmpty()) {
                    newArguments[graphQLArgumentType.name] = graphQLArgumentType.defaultValue
                }
            }
            // NOTE: this relies on internal graphql-java API and may break in the future
            val newEnv = newDataFetchingEnvironment(dataEnv)
                    .arguments(newArguments)
                    .build()
            originalDataFetcher.get(newEnv)
        }
        environment.codeRegistry.dataFetcher(coordinates, defaultValueFetcher)
        return field
    }

    override fun onArgument(environment: SchemaDirectiveWiringEnvironment<GraphQLArgument>): GraphQLArgument {
        val argument = environment.element
        val directive = environment.directive

        val default = directive.getArgument(StringEval::default.name).value as String
        return if (default.isNotEmpty()) {
            argument.transform { it.defaultValue(default) }
        } else {
            argument
        }
    }
}
