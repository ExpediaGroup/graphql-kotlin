/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.sample.query

import com.expediagroup.graphql.annotations.GraphQLContext
import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.sample.context.MyGraphQLContext
import com.expediagroup.graphql.sample.model.ContextualResponse
import org.springframework.stereotype.Component

/**
 * Example usage of [GraphQLContext] annotation. By using this annotation context parameter won't be exposed as in the
 * schema and will be automatically autowired at runtime using value from the environment.
 *
 * @see com.expediagroup.graphql.sample.context.MyGraphQLContextWebFilter
 * @see com.expediagroup.graphql.execution.FunctionDataFetcher
 */
@Component
class ContextualQuery: Query {

    @GraphQLDescription("query that uses GraphQLContext context")
    fun contextualQuery(
        @GraphQLDescription("some value that will be returned to the user")
        value: Int,
        @GraphQLContext context: MyGraphQLContext
    ): ContextualResponse = ContextualResponse(value, context.myCustomValue)
}
