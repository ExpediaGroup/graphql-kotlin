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

package com.expediagroup.graphql.examples.client.server.repository

import com.expediagroup.graphql.examples.client.server.model.BasicObject
import org.springframework.stereotype.Repository

@Repository
class BasicObjectRepository {

    private val objectRepository = mutableMapOf<Int, BasicObject>()

    fun get(id: Int): BasicObject? = objectRepository[id]

    fun add(newObject: BasicObject): BasicObject? {
        if (objectRepository.containsKey(newObject.id)) {
            throw RuntimeException("object with ID=${newObject.id} already exists")
        }
        objectRepository[newObject.id] = newObject
        return newObject
    }

    fun update(updatedObject: BasicObject): BasicObject? {
        if (!objectRepository.containsKey(updatedObject.id)) {
            throw RuntimeException("object with ID=${updatedObject.id} does not exist so it cannot be updated")
        }
        objectRepository[updatedObject.id] = updatedObject
        return updatedObject
    }

    fun remove(id: Int): BasicObject? = objectRepository.remove(id)
}
