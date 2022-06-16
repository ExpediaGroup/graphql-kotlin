package com.expediagroup.graphql.examples.server.ktor

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import io.ktor.events.EventDefinition
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * graphql-kotlin plugin for Ktor
 * state: trying out a configuration API
 * **/
val GraphQLKotlin = createApplicationPlugin(
    name = "GraphQL",
    createConfiguration = {
        KtorGraphQLConfig.init()
    },
    body = {
        val application = pluginConfig
        // TODO improve plugin
        KtorGraphQLConfig.config = pluginConfig

        val monitor = environment?.monitor
        monitor?.raise(KtorGraphQLConfigStarted, application)

        monitor?.subscribe(ApplicationStopping) {
            monitor.raise(KtorGraphQLConfigStopped, application)
        }
    }
)

class KtorGraphQLConfig private constructor() {
    companion object {
        fun init(): KtorGraphQLConfig = KtorGraphQLConfig()

        // TODO: remove
        lateinit var config: KtorGraphQLConfig
    }


    // TODO not sure that's how you do required paramters
    lateinit var queries : List<Query>
    lateinit var mutations: List<Mutation>
    lateinit var schemaGeneratorConfig: SchemaGeneratorConfig

    fun generateContextMap(operation: (ApplicationRequest) -> Map<Any, Any>) {
        generateContextLambda = operation
    }

    fun configureGraphQL(operation: GraphQL.Builder.() -> Unit) {
        configureGraphQLLambda=operation
    }

    fun endpoints(operation: KtorGraphQLEndpointConfig.() -> Unit) {
        endpoints.operation()
    }

    // Optional
    var dataLoaders: List<KotlinDataLoader<*, *>> = emptyList()

    // store things
    val endpoints = KtorGraphQLEndpointConfig()
    lateinit var graphQL : GraphQL
        private set
    lateinit var graphQLSchema : GraphQLSchema
        private set
    private var configureGraphQLLambda: GraphQL.Builder.() -> Unit
        = {}
    private var generateContextLambda: (ApplicationRequest) -> Map<Any, Any>
        = { emptyMap() }

    fun buildServer(): KtorServer {
        check(this::queries.isInitialized) { "queries is required" }
        check(this::mutations.isInitialized) { "mutations is required" }
        check(this::schemaGeneratorConfig.isInitialized) { "schemaGeneratorConfig is required" }
        graphQLSchema = toSchema(
            schemaGeneratorConfig,
            queries.map { TopLevelObject(it) },
            mutations.map { TopLevelObject(it) },
        )
        graphQL = GraphQL.newGraphQL(graphQLSchema)
            .apply(configureGraphQLLambda)
            .build()
        val requestHandler = GraphQLRequestHandler(
            graphQL,
            KotlinDataLoaderRegistryFactory(dataLoaders)
        )
        // TODO: this should come from plugin ContentNegotiation
        val mapper = jacksonObjectMapper()
        val requestParser = KtorGraphQLRequestParser(mapper)

        val generateContextMapLambda = generateContextLambda
        val contextFactory = object : GraphQLContextFactory<GraphQLContext, ApplicationRequest> {
            override suspend fun generateContextMap(request: ApplicationRequest): Map<*, Any> {
                return generateContextMapLambda(request)
            }
        }

        return KtorServer(
            mapper,
            KtorGraphQLServer(requestParser, contextFactory, requestHandler),
        )
    }
}

data class KtorGraphQLEndpointConfig(
    var enablePlayground: Boolean = true,
    var enableSdl: Boolean = true,
    var graphql: String = "graphql",
    var sdl: String = "sdl",
    var playground: String = "playground",
)

/**
 * Event definition for [KtorGraphQLConfig] Started event
 */
val KtorGraphQLConfigStarted = EventDefinition<KtorGraphQLConfig>()

/**
 * Event definition for an event that is fired when the [KtorGraphQLConfig] is going to stop
 */
val KtorGraphQLConfigStopPreparing = EventDefinition<KtorGraphQLConfig>()

/**
 * Event definition for [KtorGraphQLConfig] Stopping event
 */
val KtorGraphQLConfigStopped = EventDefinition<KtorGraphQLConfig>()

fun Application.installEndpoints(
    config: KtorGraphQLConfig
) {
    val server = config.buildServer()
    routing {
        post(config.endpoints.graphql) {
            server.handle(this.call)
        }
        if (config.endpoints.enableSdl) {
            get(config.endpoints.sdl) {
                call.respondText(config.graphQL.graphQLSchema.print())
            }
        }
        if (config.endpoints.enablePlayground) {
            get(config.endpoints.playground) {
                this.call.respondText(buildPlaygroundHtml("graphql", "subscriptions"), ContentType.Text.Html)
            }
        }
    }
}

fun buildPlaygroundHtml(graphQLEndpoint: String, subscriptionsEndpoint: String) =
    Application::class.java.classLoader.getResource("graphql-playground.html")?.readText()
        ?.replace("\${graphQLEndpoint}", graphQLEndpoint)
        ?.replace("\${subscriptionsEndpoint}", subscriptionsEndpoint)
        ?: throw IllegalStateException("graphql-playground.html cannot be found in the classpath")
