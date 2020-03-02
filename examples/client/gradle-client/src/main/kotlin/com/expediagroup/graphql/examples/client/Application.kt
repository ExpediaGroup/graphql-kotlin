package com.expediagroup.graphql.examples.client

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.generated.WidgetQuery
import kotlinx.coroutines.runBlocking
import java.net.URL

fun main() {
    runBlocking {
        val client = GraphQLClient(url = URL("http://localhost:8080/graphql"))
        val query = WidgetQuery(client)
        val result = query.widgetQuery()
        println(result)
    }
}
