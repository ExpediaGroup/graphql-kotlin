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

package com.expediagroup.graphql.examples.client.server

import com.expediagroup.graphql.examples.client.server.model.BasicObject
import com.expediagroup.graphql.examples.client.server.repository.BasicObjectRepository
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import org.springframework.stereotype.Component

@Component
class SimpleMutations(private val repository: BasicObjectRepository) : Mutation {

    @GraphQLDescription("Add object to the repository")
    fun addBasicObject(newObject: BasicObject): BasicObject? = repository.add(newObject)

    @GraphQLDescription("Update existing object in the repository")
    fun updateBasicObject(updatedObject: BasicObject): BasicObject? = repository.update(updatedObject)

    @GraphQLDescription("Delete object from repository")
    fun deleteBasicObject(id: Int): BasicObject? = repository.remove(id)
}
