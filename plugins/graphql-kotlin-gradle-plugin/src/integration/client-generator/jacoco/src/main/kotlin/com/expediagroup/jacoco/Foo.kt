package com.expediagroup.jacoco

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.jacoco.generated.JacocoQuery

class Foo(private val client: GraphQLWebClient) {

    suspend fun query(): JacocoQuery.Result? {
        val query = JacocoQuery()
        return client.execute(query).data
    }
}