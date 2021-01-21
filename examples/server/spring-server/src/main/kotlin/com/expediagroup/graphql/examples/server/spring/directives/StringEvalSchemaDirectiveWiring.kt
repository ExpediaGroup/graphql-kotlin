/*
 * Copyright 2021 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.examples.server.spring.directives

import com.expediagroup.graphql.directives.KotlinFieldDirectiveEnvironment
import com.expediagroup.graphql.directives.KotlinSchemaDirectiveEnvironment
import com.expediagroup.graphql.directives.KotlinSchemaDirectiveWiring
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition

class StringEvalSchemaDirectiveWiring : KotlinSchemaDirectiveWiring {
    private val directiveName = getDirectiveName(StringEval::class)

    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher: DataFetcher<*> = environment.getDataFetcher()

        val defaultValueFetcher = DataFetcher<Any> { dataEnv ->
            val newArguments = HashMap(dataEnv.arguments)
            environment.element.arguments.associateWith {
                dataEnv.getArgument(it.name) as String?
            }.forEach { (graphQLArgumentType, value) ->
                if (graphQLArgumentType.getDirective(directiveName).getArgument(StringEval::lowerCase.name).value as Boolean) {
                    newArguments[graphQLArgumentType.name] = value?.toLowerCase()
                }
                if (value.isNullOrEmpty()) {
                    newArguments[graphQLArgumentType.name] = graphQLArgumentType.defaultValue
                }
            }
            val newEnv = newDataFetchingEnvironment(dataEnv)
                .arguments(newArguments)
                .build()
            originalDataFetcher.get(newEnv)
        }
        environment.setDataFetcher(defaultValueFetcher)
        return field
    }

    override fun onArgument(environment: KotlinSchemaDirectiveEnvironment<GraphQLArgument>): GraphQLArgument {
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
