/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.plugin.maven

import com.expediagroup.graphql.plugin.generateClient
import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorConfig
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.io.File

/**
 * Generate GraphQL client code based on the provided GraphQL schema and target queries.
 */
@Mojo(name = "generateClient", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
class GenerateClientMojo : AbstractMojo() {

    /**
     * GraphQL schema file that will be used to generate client code.
     */
    @Parameter(defaultValue = "\${graphql.schemaFile}", name = "schemaFile", required = true)
    private lateinit var schemaFile: File

    /**
     * Target package name for generated code.
     */
    @Parameter(defaultValue = "\${graphql.packageName}", name = "packageName", required = true)
    private lateinit var packageName: String

    /**
     * Boolean flag indicating whether selection of deprecated fields is allowed or not.
     */
    @Parameter(defaultValue = "\${graphql.allowDeprecatedFields}", name = "allowDeprecatedFields")
    private var allowDeprecatedFields: Boolean = false

    /**
     * Custom scalar converter mapping.
     */
    @Parameter(name = "converters")
    private lateinit var scalarConverters: Map<String, ScalarConverter>

    @Parameter(name = "queryFileDirectory", defaultValue = "\${project.basedir}/src/main/resources")
    private lateinit var queryFileDirectory: File

    @Parameter(name = "queryFiles")
    private lateinit var queryFiles: List<File>

    @Parameter(name = "outputDirectory", defaultValue = "\${project.build.directory}/generated/sources/graphql")
    private lateinit var outputDirectory: File

    @Suppress("EXPERIMENTAL_API_USAGE")
    override fun execute() {
        log.debug("generating GraphQL client")
        val graphQLSchema = if (schemaFile.isFile) {
            schemaFile
        } else {
            throw RuntimeException("specified GraphQL schema is not a file, ${schemaFile.path}")
        }

        val targetQueryFiles: List<File> = when {
            ::queryFiles.isInitialized -> queryFiles
            else -> queryFileDirectory
                .listFiles { file -> file.extension == "graphql" }
                ?.toList() ?: throw RuntimeException("exception while looking up the query files")
        }
        if (targetQueryFiles.isEmpty()) {
            throw RuntimeException("no query files specified")
        }

        if (!outputDirectory.isDirectory && !outputDirectory.mkdirs()) {
            throw RuntimeException("failed to generate generated source directory")
        }
        val config = GraphQLClientGeneratorConfig(
            packageName = packageName,
            allowDeprecated = allowDeprecatedFields,
            scalarTypeToConverterMapping = scalarConverters.map { (key, value) -> key to ScalarConverterMapping(value.type, value.converter) }.toMap()
        )
        generateClient(config, graphQLSchema, targetQueryFiles).forEach {
            it.writeTo(outputDirectory)
        }
        log.debug("successfully generated GraphQL HTTP client")
    }
}

/**
 * Maven Plugin Property equivalent of [ScalarConverterMapping].
 *
 * Unfortunately we cannot use [ScalarConverterMapping] directly as per rules of mapping complex objects to Mojo parameters, target object has to be declared in
 * the same package as Mojo itself (otherwise we need to explicitly specify fully qualified implementation name in configuration XML block).
 *
 * @see [Guide to Configuring Plug-ins](https://maven.apache.org/guides/mini/guide-configuring-plugins.html#Mapping_Complex_Objects)
 */
data class ScalarConverter(
    /** Fully qualified class name of a custom scalar type, e.g. java.util.UUID */
    val type: String,
    /** Fully qualified class name of a custom converter used to convert to/from raw JSON and [type] */
    val converter: String
)
