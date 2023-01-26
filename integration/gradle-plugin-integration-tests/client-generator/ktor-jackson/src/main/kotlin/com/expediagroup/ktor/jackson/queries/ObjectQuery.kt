package com.expediagroup.ktor.jackson.queries

import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import java.util.UUID

class ObjectQuery : Query {

    fun objectQuery(): ExampleObject = ExampleObject(
        id = ID(UUID.randomUUID().toString()),
        description = null,
        count = 123,
        flag = false,
        choice = ExampleEnum.ONE
    )
}

data class ExampleObject(val id: ID, val description: String?, val count: Int, val flag: Boolean, val choice: ExampleEnum)

enum class ExampleEnum {
    ONE,
    TWO
}
