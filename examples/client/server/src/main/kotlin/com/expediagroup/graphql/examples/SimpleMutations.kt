package com.expediagroup.graphql.examples

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.examples.model.BasicObject
import com.expediagroup.graphql.examples.model.SimpleArgument
import com.expediagroup.graphql.examples.repository.BasicObjectRepository
import com.expediagroup.graphql.spring.operations.Mutation
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class SimpleMutations(private val repository: BasicObjectRepository) : Mutation {

    @GraphQLDescription("Add object to the repository")
    fun addBasicObject(newObject: BasicObject): BasicObject? = repository.add(newObject)

    @GraphQLDescription("Update existing object in the repository")
    fun updateBasicObject(updatedObject: BasicObject): BasicObject? = repository.update(updatedObject)

    @GraphQLDescription("Delete object from repository")
    fun deleteBasicObject(id: Int): BasicObject? = repository.remove(id)
}
