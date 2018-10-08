package com.expedia.graphql.sample

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.sample.context.MyGraphQLContextBuilder
import com.expedia.graphql.sample.dataFetchers.SpringDataFetcherFactory
import com.expedia.graphql.sample.extension.CustomSchemaGeneratorHooks
import com.expedia.graphql.sample.mutation.Mutation
import com.expedia.graphql.sample.query.Query
import com.expedia.graphql.schema.SchemaGeneratorConfig
import com.expedia.graphql.toSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import graphql.servlet.GraphQLInvocationInputFactory
import graphql.servlet.GraphQLObjectMapper
import graphql.servlet.GraphQLQueryInvoker
import graphql.servlet.ObjectMapperConfigurer
import graphql.servlet.SimpleGraphQLHttpServlet
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import javax.servlet.http.HttpServlet

@SpringBootApplication
@ComponentScan("com.expedia.graphql")
class Application {

    private val logger = LoggerFactory.getLogger(Application::class.java)

    @Bean
    fun schemaConfig(dataFetcherFactory: SpringDataFetcherFactory): SchemaGeneratorConfig = SchemaGeneratorConfig(
            supportedPackages = "com.expedia",
            hooks = CustomSchemaGeneratorHooks(),
            dataFetcherFactory = dataFetcherFactory
    )

    @Bean
    fun schema(
            queries: List<Query>,
            mutations: List<Mutation>,
            schemaConfig: SchemaGeneratorConfig
    ): GraphQLSchema {
        fun List<Any>.toTopLevelObjectDefs() = this.map {
            TopLevelObjectDef(it)
        }

        val schema = toSchema(
                queries = queries.toTopLevelObjectDefs(),
                mutations = mutations.toTopLevelObjectDefs(),
                config = schemaConfig
        )
        logger.info(SchemaPrinter().print(schema))
        return schema
    }

    @Bean
    fun contextBuilder() = MyGraphQLContextBuilder()

    @Bean
    fun graphQLInvocationInputFactory(
            schema: GraphQLSchema,
            contextBuilder: MyGraphQLContextBuilder
    ): GraphQLInvocationInputFactory = GraphQLInvocationInputFactory.newBuilder(schema)
            .withGraphQLContextBuilder(contextBuilder)
            .build()

    @Bean
    fun graphQLQueryInvoker(): GraphQLQueryInvoker = GraphQLQueryInvoker.newBuilder()
            .build()

    @Bean
    fun graphQLObjectMapper(): GraphQLObjectMapper = GraphQLObjectMapper.newBuilder()
            .withObjectMapperConfigurer(ObjectMapperConfigurer { it.registerModule(KotlinModule()) })
            .build()

    @Bean
    fun graphQLServlet(
            invocationInputFactory: GraphQLInvocationInputFactory,
            queryInvoker: GraphQLQueryInvoker,
            objectMapper: GraphQLObjectMapper
    ): SimpleGraphQLHttpServlet = SimpleGraphQLHttpServlet.newBuilder(invocationInputFactory)
            .withQueryInvoker(queryInvoker)
            .withObjectMapper(objectMapper)
            .build()

    @Bean
    fun graphQLServletRegistration(graphQLServlet: HttpServlet) = ServletRegistrationBean(graphQLServlet, "/graphql")
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}