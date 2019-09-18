package com.expediagroup.graphql.spring.base

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.spring.GraphQLAutoConfiguration
import com.expediagroup.graphql.spring.QueryHandler
import com.expediagroup.graphql.spring.operations.Query
import com.expediagroup.graphql.toSchema
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.GraphQL
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLTypeUtil
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
            .withPropertyValues("graphql.packages=com.expediagroup.graphql.spring.base")
            .run { ctx ->
                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                val schemaGeneratorConfig = ctx.getBean(SchemaGeneratorConfig::class.java)
                assertEquals(listOf("com.expediagroup.graphql.spring.base"), schemaGeneratorConfig.supportedPackages)

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

                assertThat(ctx).hasSingleBean(QueryHandler::class.java)
            }
    }

    @Test
    fun `verify schema auto configuration backs off in beans are defined by user`() {
        contextRunner.withUserConfiguration(CustomConfiguration::class.java)
            .run { ctx ->
                val customConfiguration = ctx.getBean(CustomConfiguration::class.java)

                assertThat(ctx).hasSingleBean(SchemaGeneratorConfig::class.java)
                assertThat(ctx).getBean(SchemaGeneratorConfig::class.java)
                    .isSameAs(customConfiguration.customSchemaConfig())

                assertThat(ctx).hasSingleBean(GraphQLSchema::class.java)
                assertThat(ctx).getBean(GraphQLSchema::class.java)
                    .isSameAs(customConfiguration.mySchema())

                assertThat(ctx).hasSingleBean(GraphQL::class.java)
                assertThat(ctx).getBean(GraphQL::class.java)
                    .isSameAs(customConfiguration.myGraphQL())

                assertThat(ctx).hasSingleBean(QueryHandler::class.java)
            }
    }

    @Configuration
    class BasicConfiguration {

        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()

        @Bean
        fun helloWorldQuery(): Query = BasicQuery()

        @Bean
        fun instrumentation(): Instrumentation = TracingInstrumentation()
    }

    @Configuration
    class CustomConfiguration {

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
    }

    class BasicQuery : Query {
        @Suppress("FunctionOnlyReturningConstant")
        fun hello(): String = "Hello"
    }
}
