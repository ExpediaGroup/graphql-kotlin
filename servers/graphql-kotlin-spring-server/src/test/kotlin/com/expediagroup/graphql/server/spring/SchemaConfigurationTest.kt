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

package com.expediagroup.graphql.server.spring

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.execution.SimpleKotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.server.execution.DataLoaderRegistryFactory
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.types.operations.Query
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import com.expediagroup.graphql.toSchema
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.GraphQL
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLTypeUtil
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SchemaConfigurationTest {

    private val contextRunner: ReactiveWebApplicationContextRunner = ReactiveWebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(GraphQLAutoConfiguration::class.java))

    @Test
    fun `verify schema auto configuration`() {
        contextRunner.withUserConfiguration(BasicConfiguration::class.java)
            .withPropertyValues("graphql.packages=com.expediagroup.graphql.server.spring")
            .run { ctx ->
                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                val schemaGeneratorConfig = ctx.getBean(SchemaGeneratorConfig::class.java)
                assertEquals(listOf("com.expediagroup.graphql.server.spring"), schemaGeneratorConfig.supportedPackages)

                assertThat(ctx).hasSingleBean(ObjectMapper::class.java)
                val mapper = ctx.getBean(ObjectMapper::class.java)
                assertThat(ctx).hasSingleBean(KotlinDataFetcherFactoryProvider::class.java)
                val dataFetcherFactoryProvider = ctx.getBean(KotlinDataFetcherFactoryProvider::class.java)
                val privateMapperField = SimpleKotlinDataFetcherFactoryProvider::class.java.getDeclaredField("objectMapper")
                privateMapperField.isAccessible = true
                val privateMapper = privateMapperField.get(dataFetcherFactoryProvider)
                assertEquals(mapper, privateMapper)

                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)
                val schema = ctx.getBean(GraphQLSchema::class.java)
                val query = schema.queryType
                val fields = query.fieldDefinitions
                assertEquals(1, fields.size)
                val helloWorldQuery = fields.firstOrNull { it.name == "hello" }
                assertNotNull(helloWorldQuery)
                assertEquals("String", GraphQLTypeUtil.unwrapAll(helloWorldQuery.type).name)

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                val graphQL = ctx.getBean(GraphQL::class.java)
                val result = graphQL.execute("query { hello }").toSpecification()
                assertNotNull(result["data"] as? Map<*, *>) { data ->
                    assertEquals("Hello", data["hello"])
                }
                assertNull(result["errors"])
                assertNotNull(result["extensions"])

                assertThat(ctx).hasSingleBean(DataLoaderRegistryFactory::class.java)
                assertThat(ctx).hasSingleBean(GraphQLRequestHandler::class.java)
                assertThat(ctx).hasSingleBean(GraphQLContextFactory::class.java)
            }
    }

    @Test
    fun `verify schema auto configuration backs off in beans are defined by user`() {
        contextRunner.withUserConfiguration(CustomConfiguration::class.java)
            .withPropertyValues("graphql.packages=com.expediagroup.graphql.server.spring")
            .run { ctx ->
                val customConfiguration = ctx.getBean(CustomConfiguration::class.java)
                val graphQLProperties = ctx.getBean(GraphQLConfigurationProperties::class.java)

                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                assertThat(ctx).getBean(SchemaGeneratorConfig::class.java)
                    .isSameAs(customConfiguration.customSchemaConfig())

                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)
                assertThat(ctx).getBean(GraphQLSchema::class.java)
                    .isSameAs(customConfiguration.mySchema())

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).getBean(GraphQL::class.java)
                    .isSameAs(customConfiguration.myGraphQL())

                assertThat(ctx).hasSingleBean(DataLoaderRegistryFactory::class.java)
                assertThat(ctx).getBean(DataLoaderRegistryFactory::class.java)
                    .isSameAs(customConfiguration.myDataLoaderRegistryFactory())

                assertThat(ctx).hasSingleBean(GraphQLRequestHandler::class.java)

                assertThat(ctx).hasSingleBean(SpringGraphQLContextFactory::class.java)
                assertThat(ctx).getBean(SpringGraphQLContextFactory::class.java)
                    .isSameAs(customConfiguration.myCustomContextFactory())
            }
    }

    @Configuration
    class BasicConfiguration {

        // in regular apps object mapper will be created by JacksonAutoConfiguration
        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()

        @Bean
        fun helloWorldQuery(): Query = BasicQuery()

        @Bean
        fun instrumentation(): Instrumentation = TracingInstrumentation()
    }

    @Configuration
    class CustomConfiguration {

        // in regular apps object mapper will be created by JacksonAutoConfiguration
        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()

        @Bean
        fun customSchemaConfig(): SchemaGeneratorConfig = SchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup")
        )

        @Bean
        fun mySchema(): GraphQLSchema = toSchema(
            config = customSchemaConfig(),
            queries = listOf(TopLevelObject(BasicQuery()))
        )

        @Bean
        fun myGraphQL(): GraphQL = GraphQL.newGraphQL(mySchema())
            .instrumentation(TracingInstrumentation())
            .build()

        @Bean
        fun myCustomContextFactory(): SpringGraphQLContextFactory<*> = mockk()

        @Bean
        fun myDataLoaderRegistryFactory(): DataLoaderRegistryFactory = mockk()
    }

    class BasicQuery : Query {
        @Suppress("FunctionOnlyReturningConstant")
        fun hello(): String = "Hello"
    }
}
