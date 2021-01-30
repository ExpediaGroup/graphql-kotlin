package com.expediagroup.graphql.server.spring.dataloader

import com.expediagroup.graphql.generator.execution.DefaultGraphQLContext
import com.expediagroup.graphql.types.GraphQLRequest
import com.expediagroup.graphql.types.operations.Query
import graphql.GraphqlErrorBuilder
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetchingEnvironment
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["graphql.packages=com.expediagroup.graphql.server.spring.dataloader", "graphql.spring.dataloader.enabled=true"]
)
@EnableAutoConfiguration
class SpringDataLoaderIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `throw exception when no dataloader is found`() {
        val request = GraphQLRequest(query = "query { notfound { value } }")
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectBody()
            .jsonPath("$.errors[0].message").isEqualTo("Exception while fetching data (notfound) : No data loader called DataLoaderNotFoundDataLoader was found")
    }

    @Test
    fun `verify string data loader returns correct response`() {
        val request = GraphQLRequest(query = "query { string }")
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectBody()
            .jsonPath("$.data.string").isEqualTo("Hello world")
    }

    @Test
    fun `verify list data loader returns correct response`() {
        val request = GraphQLRequest(query = "query { list }")
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectBody()
            .jsonPath("$.data.list[0]").isEqualTo("Hello world 1")
            .jsonPath("$.data.list[1]").isEqualTo("Hello world 2")
    }

    @Test
    fun `verify typed data loader returns correct response`() {
        val request = GraphQLRequest(query = "query { type { value } }")
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectBody()
            .jsonPath("$.data.type.value").isEqualTo("Hello world")
    }

    @Test
    fun `verify data loader returns correct datafetcher result`() {
        val request = GraphQLRequest(query = "query { datafetcher }")
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectBody()
            .jsonPath("$.data.datafetcher").isEqualTo("Hello world")
            .jsonPath("$.errors[0].message").isEqualTo("Test error")
    }

    @Configuration
    class TestConfiguration {

        @Bean
        fun query() = DataLoaderQuery()

        @Bean
        fun stringDataLoader() = object : KeysDataLoader<String, String> {
            override fun loaderName() = "String"

            override suspend fun getByKeys(keys: Set<String>, ctx: DefaultGraphQLContext): Map<String, String> {
                return keys.map { it to "$it world" }.toMap()
            }
        }

        @Bean
        fun listDataLoader() = object : KeysDataLoader<String, List<String>> {
            override fun loaderName() = "List"

            override suspend fun getByKeys(keys: Set<String>, ctx: DefaultGraphQLContext): Map<String, List<String>> {
                return keys.map { it to listOf("$it world 1", "$it world 2") }.toMap()
            }
        }

        class MyResponseDataLoader : KeysDataLoader<String, MyResponse> {
            override suspend fun getByKeys(keys: Set<String>, ctx: DefaultGraphQLContext): Map<String, MyResponse> {
                return keys.map { it to MyResponse("$it world") }.toMap()
            }
        }

        @Bean
        fun typeDataLoader() = MyResponseDataLoader()

        @Bean
        fun dataFetcherResultDataLoader() = object : KeysDataLoader<String, DataFetcherResult<String>> {
            override fun loaderName() = "DataFetcher"

            override suspend fun getByKeys(keys: Set<String>, ctx: DefaultGraphQLContext): Map<String, DataFetcherResult<String>> {
                val error = DataFetcherResult.Builder<String>()
                    .data("${keys.first()} world")
                    .error(GraphqlErrorBuilder.newError().message("Test error").build())
                    .build()
                return keys.map { it to error }.toMap()
            }
        }

    }

    data class DataLoaderNotFound(val value: String)
    data class MyResponse(val value: String)

    class DataLoaderQuery : Query {
        fun notfound(dfe: DataFetchingEnvironment) = dfe.load<String, DataLoaderNotFound>("test")
        fun string(dfe: DataFetchingEnvironment) = dfe.load<String, String>(key = "Hello", loaderPrefix = "String")
        fun list(dfe: DataFetchingEnvironment) = dfe.load<String, List<String>>(key = "Hello", loaderPrefix = "List")
        fun type(dfe: DataFetchingEnvironment) = dfe.load<String, MyResponse>("Hello")
        fun datafetcher(dfe: DataFetchingEnvironment) = dfe.load<String, DataFetcherResult<String>>(key = "Hello", loaderPrefix = "DataFetcher")
    }
}
