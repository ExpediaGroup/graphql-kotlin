package com.expediagroup.graphql.sample.query

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLID
import com.expediagroup.graphql.sample.mutation.Mutation
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Simple query that exposes custom scalar.
 */
@Component
class ScalarQuery: Query {

    @GraphQLDescription("generates random UUID")
    fun generateRandomUUID() = UUID.randomUUID()

    fun findPersonById(id: Int) = Person(id, "Nelson")
}

@Component
class ScalarMutation : Mutation {
    fun addPerson(person: Person): Person = person
}

data class Person(
    @GraphQLID
    val id: Int,

    val name: String
)
