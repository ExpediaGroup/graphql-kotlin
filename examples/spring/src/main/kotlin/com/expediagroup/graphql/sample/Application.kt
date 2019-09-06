package com.expediagroup.graphql.sample

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.extensions.print
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.sample.datafetchers.CustomDataFetcherFactoryProvider
import com.expediagroup.graphql.sample.datafetchers.SpringDataFetcherFactory
import com.expediagroup.graphql.sample.directives.CustomDirectiveWiringFactory
import com.expediagroup.graphql.sample.exceptions.CustomDataFetcherExceptionHandler
import com.expediagroup.graphql.sample.extension.CustomSchemaGeneratorHooks
import com.expediagroup.graphql.sample.mutation.Mutation
import com.expediagroup.graphql.sample.query.Query
import com.expediagroup.graphql.sample.subscriptions.Subscription
import com.expediagroup.graphql.toSchema
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.SubscriptionExecutionStrategy
import graphql.schema.GraphQLSchema
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import javax.validation.Validator

@SpringBootApplication
class Application {

    private val logger = LoggerFactory.getLogger(Application::class.java)

    @Bean
    fun wiringFactory() = CustomDirectiveWiringFactory()

    @Bean
    fun hooks(validator: Validator, wiringFactory: KotlinDirectiveWiringFactory) =
        CustomSchemaGeneratorHooks(validator, wiringFactory)

    @Bean
    fun dataFetcherFactoryProvider(springDataFetcherFactory: SpringDataFetcherFactory, hooks: SchemaGeneratorHooks) =
        CustomDataFetcherFactoryProvider(springDataFetcherFactory, hooks)

    @Bean
    fun schemaConfig(hooks: SchemaGeneratorHooks, dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider): SchemaGeneratorConfig = SchemaGeneratorConfig(
        supportedPackages = listOf("com.expediagroup"),
        hooks = hooks,
        dataFetcherFactoryProvider = dataFetcherFactoryProvider
    )

    @Bean
    fun schema(
        queries: List<Query>,
        mutations: List<Mutation>,
        subscriptions: List<Subscription>,
        schemaConfig: SchemaGeneratorConfig
    ): GraphQLSchema {
        fun List<Any>.toTopLevelObjects() = this.map {
            TopLevelObject(it)
        }

        val schema = toSchema(
            config = schemaConfig,
            queries = queries.toTopLevelObjects(),
            mutations = mutations.toTopLevelObjects(),
            subscriptions = subscriptions.toTopLevelObjects()
        )

        logger.info(schema.print())

        return schema
    }

    @Bean
    fun dataFetcherExceptionHandler(): DataFetcherExceptionHandler = CustomDataFetcherExceptionHandler()

    @Bean
    fun subscriptionHandler(graphQL: GraphQL) = SubscriptionHandler(graphQL)

    @Bean
    fun websocketHandlerAdapter() = WebSocketHandlerAdapter()

    @Bean
    fun graphQL(
        schema: GraphQLSchema,
        dataFetcherExceptionHandler: DataFetcherExceptionHandler
    ): GraphQL = GraphQL.newGraphQL(schema)
        .queryExecutionStrategy(AsyncExecutionStrategy(dataFetcherExceptionHandler))
        .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetcherExceptionHandler))
        .subscriptionExecutionStrategy(SubscriptionExecutionStrategy(dataFetcherExceptionHandler))
        .build()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
