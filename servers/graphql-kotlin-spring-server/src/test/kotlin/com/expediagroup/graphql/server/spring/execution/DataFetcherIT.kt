/*
 * Copyright 2021 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expediagroup.graphql.server.spring.execution

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
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
    properties = ["graphql.packages=com.expediagroup.graphql.server.spring.execution"]
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
            override fun parseValue(input: Any): LocalDate = try {
                LocalDate.parse(serialize(input))
            } catch (e: Exception) {
                throw CoercingParseValueException("Cannot parse value $input to LocalDate", e)
            }

            override fun parseLiteral(input: Any): LocalDate = try {
                LocalDate.parse((input as? StringValue)?.value)
            } catch (e: Exception) {
                throw CoercingParseLiteralException("Cannot parse literal $input to LocalDate", e)
            }

            override fun serialize(dataFetcherResult: Any): String = dataFetcherResult.toString()
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
