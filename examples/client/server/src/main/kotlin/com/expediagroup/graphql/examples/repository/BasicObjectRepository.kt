package com.expediagroup.graphql.examples.repository

import com.expediagroup.graphql.examples.model.BasicObject
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