/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.spring

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.federation.directives.ExternalDirective
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import com.expediagroup.graphql.spring.execution.QueryHandler
import com.expediagroup.graphql.spring.operations.Query
import com.expediagroup.graphql.toSchema
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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

class FederationConfigurationTest {

    private val contextRunner: ReactiveWebApplicationContextRunner = ReactiveWebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(GraphQLAutoConfiguration::class.java))

    @Test
    fun `verify federated schema auto configuration`() {
        contextRunner.withUserConfiguration(FederatedConfiguration::class.java)
            .withPropertyValues("graphql.packages=com.expediagroup.graphql.spring", "graphql.federation.enabled=true")
            .run { ctx ->
                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                val schemaGeneratorConfig = ctx.getBean(SchemaGeneratorConfig::class.java)
                assertEquals(listOf("com.expediagroup.graphql.spring"), schemaGeneratorConfig.supportedPackages)

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
                assertNotNull(widgetType.directives.firstOrNull { it.name == "key" })
                assertNotNull(widgetType.directives.firstOrNull { it.name == "extends" })

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).hasSingleBean(QueryHandler::class.java)
                assertThat(ctx).hasSingleBean(GraphQLContextFactory::class.java)
            }
    }

    @Test
    fun `verify federated schema auto configuration backs off in beans are defined by user`() {
        contextRunner.withUserConfiguration(CustomFederatedConfiguration::class.java)
            .withPropertyValues("graphql.packages=com.expediagroup.graphql.spring", "graphql.federation.enabled=true")
            .run { ctx ->
                val customConfiguration = ctx.getBean(CustomFederatedConfiguration::class.java)

                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                assertThat(ctx).getBean(SchemaGeneratorConfig::class.java)
                    .isSameAs(customConfiguration.customSchemaConfig())

                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)
                assertThat(ctx).getBean(GraphQLSchema::class.java)
                    .isSameAs(customConfiguration.mySchema())

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).hasSingleBean(QueryHandler::class.java)
                assertThat(ctx).hasSingleBean(GraphQLContextFactory::class.java)
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
            supportedPackages = listOf("com.expediagroup"),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry())
        )

        @Bean
        fun mySchema(): GraphQLSchema = toSchema(
            config = customSchemaConfig(),
            queries = listOf(TopLevelObject(FederatedQuery()))
        )
    }

    class FederatedQuery : Query {
        fun widget(): Widget = Widget(1, "hello")
    }

    @ExtendsDirective
    @KeyDirective(fields = FieldSet("id"))
    data class Widget(@ExternalDirective val id: Int, val name: String)
}
