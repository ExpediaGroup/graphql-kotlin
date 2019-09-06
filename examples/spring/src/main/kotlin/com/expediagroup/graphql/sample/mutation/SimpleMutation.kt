package com.expediagroup.graphql.sample.mutation

import com.expediagroup.graphql.annotations.GraphQLDescription
import org.springframework.stereotype.Component

/**
 * Example of simple mutation. Code wise, mutations are created in the same fashion as queries. Only difference is in
 * semantics - mutations imply underlying data gets modified.
 */
@Component
class SimpleMutation: Mutation {

    private val data: MutableList<String> = mutableListOf()

    @GraphQLDescription("add value to a list and return resulting list")
    fun addToList(entry: String): MutableList<String> {
        data.add(entry)
        return data
    }
}
