/*
 * Copyright 2026 Expedia, Inc
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

@file:OptIn(ExperimentalSemanticIntrospection::class)

package com.expediagroup.graphql.server.spring

import com.expediagroup.graphql.generator.semantic.ExperimentalSemanticIntrospection
import com.expediagroup.graphql.generator.semantic.SEMANTIC_SEARCH_FIELD
import com.expediagroup.graphql.generator.semantic.SemanticSearchResolver
import com.expediagroup.graphql.generator.semantic.SemanticSearchResult
import com.expediagroup.graphql.server.operations.Query
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SemanticIntrospectionConfigurationTest {

    private val contextRunner = ReactiveWebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(GraphQLAutoConfiguration::class.java))
        .withPropertyValues("graphql.packages=com.expediagroup.graphql.server.spring")

    @Test
    fun `adds _search to the schema the GraphQL bean runs when a resolver bean exists`() {
        contextRunner.withUserConfiguration(QueryConfiguration::class.java, ResolverConfiguration::class.java).run { ctx ->
            assertThat(ctx).hasNotFailed()
            assertNotNull(ctx.getBean(GraphQLSchema::class.java).queryType.getFieldDefinition(SEMANTIC_SEARCH_FIELD))
            val result = ctx.getBean(GraphQL::class.java).execute("""{ _search(query: "hello") { coordinate } }""")
            assertEquals(mapOf("_search" to listOf(mapOf("coordinate" to "Query.hello"))), result.getData())
        }
    }

    @Test
    fun `leaves the schema alone without a resolver or with introspection disabled`() {
        contextRunner.withUserConfiguration(QueryConfiguration::class.java).run { ctx ->
            assertNull(ctx.getBean(GraphQLSchema::class.java).queryType.getFieldDefinition(SEMANTIC_SEARCH_FIELD))
        }
        contextRunner.withUserConfiguration(QueryConfiguration::class.java, ResolverConfiguration::class.java)
            .withPropertyValues("graphql.introspection.enabled=false")
            .run { ctx -> assertNull(ctx.getBean(GraphQLSchema::class.java).queryType.getFieldDefinition(SEMANTIC_SEARCH_FIELD)) }
    }

    @Test
    fun `works with federation and boolean spellings of the introspection flag`() {
        contextRunner.withUserConfiguration(QueryConfiguration::class.java, ResolverConfiguration::class.java)
            .withPropertyValues("graphql.federation.enabled=true", "graphql.introspection.enabled=on")
            .run { ctx ->
                assertThat(ctx).hasNotFailed()
                assertNotNull(ctx.getBean(GraphQLSchema::class.java).queryType.getFieldDefinition(SEMANTIC_SEARCH_FIELD))
            }
    }

    @Configuration
    class QueryConfiguration {
        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()

        @Bean
        fun helloQuery(): Query = HelloQuery()
    }

    @Configuration
    class ResolverConfiguration {
        @Bean
        fun resolver() = SemanticSearchResolver { _, _ -> listOf(SemanticSearchResult("Query.hello", listOf(listOf("Query.hello")), 1.0, "0")) }
    }

    class HelloQuery : Query {
        fun hello(): String = "hello"
    }
}
