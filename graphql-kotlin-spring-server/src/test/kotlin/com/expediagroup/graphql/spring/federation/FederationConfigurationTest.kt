package com.expediagroup.graphql.spring.federation

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.federation.directives.ExternalDirective
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective
import com.expediagroup.graphql.spring.GraphQLAutoConfiguration
import com.expediagroup.graphql.spring.QueryHandler
import com.expediagroup.graphql.spring.operations.Query
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.GraphQL
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FederationConfigurationTest {

    private val contextRunner: ApplicationContextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(GraphQLAutoConfiguration::class.java))
        .withBean(ObjectMapper::class.java, jacksonObjectMapper())
        .withBean(FederatedQuery()::class.java)

    @Test
    fun `verify federated schema auto configuration`() {
        contextRunner.withPropertyValues("graphql.packages=com.expediagroup.graphql.spring.federation", "graphql.federation.enabled=true")
            .run { ctx ->
                val schemaGeneratorConfig = ctx.getBean(SchemaGeneratorConfig::class.java)
                assertEquals(listOf("com.expediagroup.graphql.spring.federation"), schemaGeneratorConfig.supportedPackages)

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

                ctx.getBean(GraphQL::class.java)
                ctx.getBean(QueryHandler::class.java)
            }
    }

    class FederatedQuery : Query {
        fun widget(): Widget = Widget(1, "hello")
    }

    @ExtendsDirective
    @KeyDirective(fields = FieldSet("id"))
    data class Widget(@ExternalDirective val id: Int, val name: String)
}
