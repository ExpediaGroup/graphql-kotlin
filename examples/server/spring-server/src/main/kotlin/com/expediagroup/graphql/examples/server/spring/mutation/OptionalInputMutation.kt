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

package com.expediagroup.graphql.examples.server.spring.mutation

import com.expediagroup.graphql.server.operations.Mutation
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class OptionalInputMutation : Mutation {

    /**
     * Example of how to check the difference between a client not sending a value
     * or explicitly passing in null
     */
    fun optionalInput(value: String?, dataFetchingEnvironment: DataFetchingEnvironment): String =
        if (dataFetchingEnvironment.containsArgument("value")) {
            "The value was $value"
        } else {
            "The value was undefined"
        }
}
