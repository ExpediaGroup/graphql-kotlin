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

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.GraphQL
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLTypeUtil
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.CompletableFuture
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

                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)
                val schema = ctx.getBean(GraphQLSchema::class.java)
                val query = schema.queryType
                val fields = query.fieldDefinitions
                assertEquals(2, fields.size)
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

                assertThat(ctx).hasSingleBean(KotlinDataLoaderRegistryFactory::class.java)
                val registryFactory = ctx.getBean(KotlinDataLoaderRegistryFactory::class.java)
                val registry = registryFactory.generate()
                assertEquals(1, registry.dataLoaders.size)
                assertEquals(FooDataLoader.name, registry.keys.first())
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
                assertThat(ctx).hasSingleBean(GraphQLConfigurationProperties::class.java)

                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                assertThat(ctx).getBean(SchemaGeneratorConfig::class.java)
                    .isSameAs(customConfiguration.customSchemaConfig())

                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)
                assertThat(ctx).getBean(GraphQLSchema::class.java)
                    .isSameAs(customConfiguration.mySchema())

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).getBean(GraphQL::class.java)
                    .isSameAs(customConfiguration.myGraphQL())

                assertThat(ctx).hasSingleBean(KotlinDataLoaderRegistryFactory::class.java)
                assertThat(ctx).getBean(KotlinDataLoaderRegistryFactory::class.java)
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

        @Bean
        fun dataLoader(): FooDataLoader = FooDataLoader()
    }

    @Configuration
    class CustomConfiguration {

        // in regular apps object mapper will be created by JacksonAutoConfiguration
        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()

        @Bean
        fun customSchemaConfig(): SchemaGeneratorConfig = SchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.server.spring")
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
        fun myCustomContextFactory(): SpringGraphQLContextFactory = mockk()

        @Bean
        fun myDataLoaderRegistryFactory(): KotlinDataLoaderRegistryFactory = mockk()
    }

    class BasicQuery : Query {
        @Suppress("FunctionOnlyReturningConstant")
        fun hello(): String = "Hello"

        @GraphQLDescription("Basic implementation of how to use data loaders to return some value based off keys")
        fun createFoo(value: String, dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<Foo> {
            return dataFetchingEnvironment.getValueFromDataLoader(FooDataLoader.name, value)
        }
    }

    class Foo(val value: String)

    class FooDataLoader : KotlinDataLoader<String, Foo> {
        companion object {
            const val name = "FooDataLoader"
        }

        override val dataLoaderName = name
        override fun getDataLoader(): DataLoader<String, Foo> = DataLoaderFactory.newDataLoader { keys ->
            CompletableFuture.supplyAsync {
                keys.mapNotNull { Foo(it) }
            }
        }
    }
}
