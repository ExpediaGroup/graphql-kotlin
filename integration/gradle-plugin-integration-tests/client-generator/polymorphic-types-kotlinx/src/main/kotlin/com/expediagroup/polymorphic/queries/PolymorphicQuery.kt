package com.expediagroup.polymorphic.queries

import com.expediagroup.graphql.server.operations.Query
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

class PolymorphicQuery : Query {

    private val logger: Logger = LoggerFactory.getLogger(PolymorphicQuery::class.java)

    fun interfaceQuery(input: String? = null): BasicInterface? = when (input) {
        "foo" -> FooImplementation(id = UUID.randomUUID().toString(), foo = input)
        "bar" -> BarImplementation(id = UUID.randomUUID().toString(), bar = input)
        else -> null
    }

    fun unionQuery(input: String? = null): BasicUnion? = when(input) {
        "foo" -> Foo(input)
        "bar" -> Bar(input)
        "baz" -> Baz(input)
        else -> null
    }
}
