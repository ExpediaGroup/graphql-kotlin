/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.examples

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.examples.model.BasicObject
import com.expediagroup.graphql.examples.model.SimpleArgument
import com.expediagroup.graphql.spring.operations.Mutation
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class SimpleMutations: Mutation {
    private val random = Random

    @GraphQLDescription("Example of a muation")
    fun simpleMutation(update: SimpleArgument): BasicObject = BasicObject(id = random.nextInt(), name = "mutation")
}
