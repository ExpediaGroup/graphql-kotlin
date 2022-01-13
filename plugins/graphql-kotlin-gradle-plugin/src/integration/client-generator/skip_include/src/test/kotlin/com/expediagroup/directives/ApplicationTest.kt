package com.expediagroup.directives

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.directives.generated.IncludeSkipQuery
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest(@LocalServerPort private val port: Int) {

    @Test
    fun `verify include and skip directives are honored by client`() = runBlocking {
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

        val response = client.execute(includeQuery)
        val simpleResponse = response.data?.simpleQuery
        assertNotNull(simpleResponse)
        assertNotNull(UUID.fromString(simpleResponse))

        val included = response.data?.included
        assertNotNull(included)
        assertNotNull(UUID.fromString(included))
        val skipped = response.data?.skipped
        assertNotNull(skipped)
        assertNotNull(UUID.fromString(skipped))
    }

}
