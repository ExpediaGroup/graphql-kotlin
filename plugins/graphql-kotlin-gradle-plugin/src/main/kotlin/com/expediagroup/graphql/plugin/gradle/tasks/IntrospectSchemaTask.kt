package com.expediagroup.graphql.plugin.gradle.tasks

import graphql.introspection.IntrospectionQuery.INTROSPECTION_QUERY
import graphql.introspection.IntrospectionResultToSchema
import graphql.schema.idl.SchemaPrinter
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

internal const val INTROSPECT_SCHEMA_TASK: String = "introspectSchema"

@Suppress("UnstableApiUsage")
open class IntrospectSchemaTask : DefaultTask() {

    @Input
    @Option(option = "endpoint", description = "target GraphQL endpoint")
    val endpoint: Property<String> = project.objects.property(String::class.java)

    @Input
    @Option(option = "outputFileName", description = "target schema file name")
    val outputFileName: Property<String> = project.objects.property(String::class.java)

    @OutputFile
    val outputFile: Provider<RegularFile> = outputFileName.flatMap { name -> project.layout.buildDirectory.file(name) }

    init {
        group = "GraphQL"
        description = "Run introspection query against target GraphQL endpoint and save schema locally."

        outputFileName.convention("schema.graphql")
    }

    @KtorExperimentalAPI
    @TaskAction
    fun introspectSchema() {
        logger.debug("starting introspection task against ${endpoint.get()}")
        runBlocking {
            HttpClient(engineFactory = CIO) {
                install(feature = JsonFeature)
            }.use { client ->
                val introspectionResult = client.post<Map<String, Any?>> {
                    url(endpoint.get())
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    body = mapOf(
                        "query" to INTROSPECTION_QUERY,
                        "operationName" to "IntrospectionQuery"
                    )
                }

                @Suppress("UNCHECKED_CAST")
                val graphQLDocument = IntrospectionResultToSchema().createSchemaDefinition(introspectionResult["data"] as? Map<String, Any?>)
                val options = SchemaPrinter.Options.defaultOptions()
                    .includeScalarTypes(true)
                    .includeExtendedScalarTypes(true)
                    .includeSchemaDefinition(true)
                    .includeDirectives(true)
                outputFile.get().asFile.writeText(SchemaPrinter(options).print(graphQLDocument))
            }
        }
    }
}
