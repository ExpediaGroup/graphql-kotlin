/*
 * Copyright 2019 Expedia Group, Inc.
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
