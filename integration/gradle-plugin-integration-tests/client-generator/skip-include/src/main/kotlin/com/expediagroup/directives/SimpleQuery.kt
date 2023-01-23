package com.expediagroup.directives

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SimpleQuery : Query {

    fun simpleQuery(): String = UUID.randomUUID().toString()
}
