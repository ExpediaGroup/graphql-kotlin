package com.expedia.graphql.sample.directives

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironmentBuilder
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiringEnvironment

class StringEvalDirectiveWiring : DirectiveWiring {
    private val directiveName = getDirectiveName(StringEval::class)

    override fun isApplicable(environment: SchemaDirectiveWiringEnvironment<*>): Boolean {
        val element = environment.element
        return when (element) {
            is GraphQLFieldDefinition -> element.getDirective(directiveName) != null || element.arguments.any { it.getDirective(directiveName) != null }
            is GraphQLArgument -> element.getDirective(directiveName) != null
            else -> false
        }
    }

    override fun onField(wiringEnv: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {
        val field = wiringEnv.element
        val originalDataFetcher: DataFetcher<Any> = field.dataFetcher

        val defaultValueFetcher = DataFetcher<Any> { dataEnv ->
            val newArguments = HashMap(dataEnv.arguments)
            wiringEnv.element.arguments.associate {
                Pair(it, dataEnv.getArgument(it.name) as String?)
            }.forEach { (graphQLArgumentType, value) ->
                if (graphQLArgumentType.getDirective(directiveName).getArgument(StringEval::lowerCase.name).value as Boolean) {
                    newArguments[graphQLArgumentType.name] = value?.toLowerCase()
                }
                if (value.isNullOrEmpty()) {
                    newArguments[graphQLArgumentType.name] = graphQLArgumentType.defaultValue
                }
            }
            val newEnv = DataFetchingEnvironmentBuilder.newDataFetchingEnvironment(dataEnv)
                    .arguments(newArguments)
                    .build()
            originalDataFetcher.get(newEnv)
        }
        return field.transform { it.dataFetcher(defaultValueFetcher) }
    }

    override fun onArgument(wiringEnv: SchemaDirectiveWiringEnvironment<GraphQLArgument>): GraphQLArgument {
        val argument = wiringEnv.element
        val directive = wiringEnv.directive

        val default = directive.getArgument(StringEval::default.name).value as String
        return if (default.isNotEmpty()) {
            argument.transform { it.defaultValue(default) }
        } else {
            argument
        }
    }
}