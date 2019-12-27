package com.expediagroup.graphql.spring.execution

import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.spring.model.GraphQLRequest
import com.expediagroup.graphql.spring.operations.Query
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["graphql.packages=com.expediagroup.graphql.spring.execution"]
)
@EnableAutoConfiguration
class DataFetcherIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify custom jackson bindings work with function data fetcher`() {
        val request = GraphQLRequest(query = "query { postWidget(widget: { id: 1, date: \"2020-01-01\" }) }")
        testClient.post()
            .uri("/graphql")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectBody()
            .jsonPath("$.data.postWidget").isEqualTo("JUNIT: widget [ID: 1, date: 2020-01-01]")
    }

    @Configuration
    class TestConfiguration {

        @Bean
        fun query(): Query = CustomQuery()

        @Bean
        fun customHook(): SchemaGeneratorHooks = object : SchemaGeneratorHooks {
            override fun willGenerateGraphQLType(type: KType): GraphQLType? = if (type.jvmErasure == LocalDate::class) {
                localDateType
            } else {
                super.willGenerateGraphQLType(type)
            }
        }

        @Bean
        fun objectMapperCustomizer() = Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder.modulesToInstall(JavaTimeModule())
        }

        private val localDateType = GraphQLScalarType.newScalar()
            .name("LocalDate")
            .description("A type representing local date")
            .coercing(LocalDateCoercing)
            .build()

        private object LocalDateCoercing : Coercing<LocalDate, String> {
            override fun parseValue(input: Any?): LocalDate = LocalDate.parse(serialize(input))

            override fun parseLiteral(input: Any?): LocalDate? = LocalDate.parse((input as? StringValue)?.value)

            override fun serialize(dataFetcherResult: Any?): String = dataFetcherResult.toString()
        }
    }

    class CustomQuery : Query {
        fun postWidget(widget: Widget): String = "JUNIT: widget [ID: ${widget.id}, date: ${widget.date}]"
    }

    data class Widget(
        val id: Int,
        val date: LocalDate
    )
}
