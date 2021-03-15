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

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import org.springframework.stereotype.Component

/**
 * Example of simple mutation. Code wise, mutations are created in the same fashion as queries. Only difference is in
 * semantics - mutations imply underlying data gets modified.
 */
@Component
class SimpleMutation : Mutation {

    private val data: MutableList<String> = mutableListOf()

    @GraphQLDescription("add value to a list and return resulting list")
    fun addToList(entry: String): MutableList<String> {
        data.add(entry)
        return data
    }
}
