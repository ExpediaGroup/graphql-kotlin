package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.examples.server.spring.GRAPHQL_ENDPOINT
import com.expediagroup.graphql.examples.server.spring.GRAPHQL_MEDIA_TYPE
import com.expediagroup.graphql.examples.server.spring.verifyData
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OptionalInputQueryIT(@Autowired private val testClient: WebTestClient) {
    @Test
    fun `verify optionalInputWithLists`() {
        val query = "optionalListInput"
        val expectedData = "[111111]"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                """query { $query(optionalInput: [{
                |   number: 111111,
                |   }]
                |) }""".trimMargin()
            )
            .exchange()
            .verifyData(query, expectedData)
    }
}
