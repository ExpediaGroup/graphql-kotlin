package com.expediagroup.graphql.spring.base

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.spring.GraphQLAutoConfiguration
import com.expediagroup.graphql.spring.QueryHandler
import com.expediagroup.graphql.spring.operations.Query
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLTypeUtil
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SchemaConfigurationTest {

    private val contextRunner: ApplicationContextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(GraphQLAutoConfiguration::class.java))
        .withBean(ObjectMapper::class.java, jacksonObjectMapper())
        .withBean(BasicQuery()::class.java)

    @Test
    fun `verify schema auto configuration`() {
        contextRunner.withPropertyValues("graphql.packages=com.expediagroup.graphql.spring.base")
            .run { ctx ->
                val schemaGeneratorConfig = ctx.getBean(SchemaGeneratorConfig::class.java)
                assertEquals(listOf("com.expediagroup.graphql.spring.base"), schemaGeneratorConfig.supportedPackages)

                val schema = ctx.getBean(GraphQLSchema::class.java)
                val query = schema.queryType
                val fields = query.fieldDefinitions
                assertEquals(1, fields.size)
                val helloWorldQuery = fields.firstOrNull { it.name == "hello" }
                assertNotNull(helloWorldQuery)
                assertEquals("String", GraphQLTypeUtil.unwrapAll(helloWorldQuery.type).name)

                ctx.getBean(GraphQL::class.java)
                ctx.getBean(QueryHandler::class.java)
            }
    }

    class BasicQuery : Query {
        @Suppress("FunctionOnlyReturningConstant")
        fun hello(): String = "Hello"
    }
}
