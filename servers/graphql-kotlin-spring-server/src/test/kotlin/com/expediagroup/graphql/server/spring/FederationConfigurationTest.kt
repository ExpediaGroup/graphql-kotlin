/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.server.spring

import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation.FEDERATED_TRACING_HEADER_NAME
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation.FEDERATED_TRACING_HEADER_VALUE
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.ExtendsDirective
import com.expediagroup.graphql.generator.federation.directives.ExternalDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.operations.Query
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FederationConfigurationTest {

    private val contextRunner: ReactiveWebApplicationContextRunner = ReactiveWebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(GraphQLAutoConfiguration::class.java))

    @Test
    fun `verify federated schema auto configuration`() {
        contextRunner.withUserConfiguration(FederatedConfiguration::class.java)
            .withPropertyValues("graphql.packages=com.expediagroup.graphql.server.spring", "graphql.federation.enabled=true")
            .run { ctx ->
                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                val schemaGeneratorConfig = ctx.getBean(SchemaGeneratorConfig::class.java)
                assertEquals(listOf("com.expediagroup.graphql.server.spring"), schemaGeneratorConfig.supportedPackages)

                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)
                val schema = ctx.getBean(GraphQLSchema::class.java)
                val query = schema.queryType
                val fields = query.fieldDefinitions
                assertEquals(3, fields.size)
                val federatedQuery = fields.firstOrNull { it.name == "widget" }
                assertNotNull(federatedQuery)
                val serviceQuery = fields.firstOrNull { it.name == "_service" }
                assertNotNull(serviceQuery)
                val entitiesQuery = fields.firstOrNull { it.name == "_entities" }
                assertNotNull(entitiesQuery)

                val widgetType = schema.getType("Widget") as? GraphQLObjectType
                assertNotNull(widgetType)
                assertNotNull(widgetType.appliedDirectives.firstOrNull { it.name == "key" })
                assertNotNull(widgetType.appliedDirectives.firstOrNull { it.name == "extends" })

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).hasSingleBean(GraphQLRequestHandler::class.java)
                assertThat(ctx).hasSingleBean(GraphQLContextFactory::class.java)
            }
    }

    @Test
    fun `verify federated schema auto configuration backs off in beans are defined by user`() {
        contextRunner.withUserConfiguration(CustomFederatedConfiguration::class.java)
            .withPropertyValues("graphql.packages=com.expediagroup.graphql.server.spring", "graphql.federation.enabled=true")
            .run { ctx ->
                val customConfiguration = ctx.getBean(CustomFederatedConfiguration::class.java)

                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                assertThat(ctx).getBean(SchemaGeneratorConfig::class.java)
                    .isSameAs(customConfiguration.customSchemaConfig())

                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)
                assertThat(ctx).getBean(GraphQLSchema::class.java)
                    .isSameAs(customConfiguration.mySchema())

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).hasSingleBean(GraphQLRequestHandler::class.java)
                assertThat(ctx).hasSingleBean(GraphQLContextFactory::class.java)
            }
    }

    @Test
    fun `verify federated schema execution with federated tracing`() {
        contextRunner.withUserConfiguration(FederatedConfiguration::class.java)
            .withPropertyValues(
                "graphql.packages=com.expediagroup.graphql.server.spring",
                "graphql.federation.enabled=true",
                "graphql.federation.tracing.enabled=true"
            )
            .run { ctx ->
                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).hasSingleBean(FederatedTracingInstrumentation::class.java)

                val graphql = ctx.getBean(GraphQL::class.java)
                val input = ExecutionInput.newExecutionInput()
                    .query("query { widget { id name } }")
                    .graphQLContext(mapOf(FEDERATED_TRACING_HEADER_NAME to FEDERATED_TRACING_HEADER_VALUE))
                    .build()

                val result = graphql.execute(input).toSpecification()
                val data = assertNotNull(result["data"] as? Map<*, *>)
                val widget = assertNotNull(data["widget"] as? Map<*, *>)
                assertEquals(1, widget["id"])
                assertEquals("hello", widget["name"])

                assertNull(result["errors"])
                val extensions = assertNotNull(result["extensions"] as? Map<*, *>)
                assertNotNull(extensions[FEDERATED_TRACING_HEADER_VALUE])
            }
    }

    @Configuration
    class FederatedConfiguration {

        // in regular apps object mapper will be created by JacksonAutoConfiguration
        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()

        @Bean
        fun federatedQuery(): Query = FederatedQuery()
    }

    @Configuration
    class CustomFederatedConfiguration {

        // in regular apps object mapper will be created by JacksonAutoConfiguration
        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()

        @Bean
        fun customSchemaConfig(): FederatedSchemaGeneratorConfig = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.server.spring"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        @Bean
        fun mySchema(): GraphQLSchema = toSchema(
            config = customSchemaConfig(),
            queries = listOf(TopLevelObject(FederatedQuery()))
        )
    }

    @Suppress("unused")
    class FederatedQuery : Query {
        fun widget(): Widget = Widget(1, "hello")
    }

    @ExtendsDirective
    @KeyDirective(fields = FieldSet("id"))
    data class Widget(@ExternalDirective val id: Int, val name: String)
}
