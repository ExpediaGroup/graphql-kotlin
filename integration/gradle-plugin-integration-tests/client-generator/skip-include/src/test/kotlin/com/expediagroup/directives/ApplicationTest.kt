package com.expediagroup.directives

import com.expediagroup.directives.generated.IncludeSkipQuery
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import java.util.UUID
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest(@LocalServerPort private val port: Int) {

    @Test
    fun `verify include and skip directives are honored by client`(): Unit = runBlocking {
        val client = GraphQLWebClient(url = "http://localhost:$port/graphql")

        val skippedQuery = IncludeSkipQuery(variables = IncludeSkipQuery.Variables(
            includeCondition = false,
            skipCondition = true
        ))

        val response = client.execute(skippedQuery)
        val simpleResponse = response.data?.simpleQuery
        assertNotNull(simpleResponse)
        assertNotNull(UUID.fromString(simpleResponse))

        val included = response.data?.included
        assertNull(included)
        val skipped = response.data?.skipped
        assertNull(skipped)

        val includeQuery = IncludeSkipQuery(variables = IncludeSkipQuery.Variables(
            includeCondition = true,
            skipCondition = false
        ))

        val nonNullResponse = client.execute(includeQuery)
        val simpleResponseNonNull = nonNullResponse.data?.simpleQuery
        assertNotNull(simpleResponseNonNull)
        assertNotNull(UUID.fromString(simpleResponseNonNull))

        val includedNonNull = nonNullResponse.data?.included
        assertNotNull(includedNonNull)
        assertNotNull(UUID.fromString(includedNonNull))
        val skippedNonNull = nonNullResponse.data?.skipped
        assertNotNull(skippedNonNull)
        assertNotNull(UUID.fromString(skippedNonNull))
    }
}
