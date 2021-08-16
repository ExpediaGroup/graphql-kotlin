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

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition

class SpecificValueOnlySchemaDirectiveWiring : KotlinSchemaDirectiveWiring {

    @Throws(RuntimeException::class)
    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher: DataFetcher<*> = environment.getDataFetcher()

        val supportedValue = environment.directive.getArgument("value")?.argumentValue?.value?.toString() ?: ""

        val cakeOnlyFetcher = DataFetcher<Any> { dataEnv ->
            val strArg: String? = dataEnv.getArgument(environment.element.arguments[0].name) as String?
            if (!supportedValue.equals(other = strArg, ignoreCase = true)) {
                throw RuntimeException("Unsupported value, expected=$supportedValue actual=$strArg")
            }
            originalDataFetcher.get(dataEnv)
        }
        environment.setDataFetcher(cakeOnlyFetcher)
        return field
    }
}
