package com.expediagroup.graphql.examples.client

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.generated.ExampleQuery
import kotlinx.coroutines.runBlocking
import java.net.URL

fun main() {
    runBlocking {
        val client = GraphQLClient(url = URL("http://localhost:8080/graphql"))
        val query = ExampleQuery(client)
        val result = query.exampleQuery(ExampleQuery.Variables(null))
        println(result)
    }
}
