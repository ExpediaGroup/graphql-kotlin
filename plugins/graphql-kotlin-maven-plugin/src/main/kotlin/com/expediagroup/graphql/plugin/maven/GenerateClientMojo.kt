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
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorConfig
import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

/**
 * Generate GraphQL client code based on the provided GraphQL schema and target queries.
 */
@Mojo(name = "generateClient", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
class GenerateClientMojo : AbstractMojo() {

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private lateinit var project: MavenProject

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
     * Boolean flag indicating whether selection of deprecated fields is allowed or not, defaults to false.
     */
    @Parameter(defaultValue = "\${graphql.allowDeprecatedFields}", name = "allowDeprecatedFields")
    private var allowDeprecatedFields: Boolean = false

    /**
     * Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to
     * serialize/deserialize values.
     *
     * ```xml
     * <converters>
     *   <!-- custom scalar UUID type -->
     *   <UUID>
     *     <!-- fully qualified Java class name of a custom scalar type -->
     *     <type>java.util.UUID</type>
     *     <!-- fully qualified Java class name of a custom com.expediagroup.graphql.client.converter.ScalarConverter
     *        used to convert to/from raw JSON and scalar type -->
     *     <converter>com.example.UUIDScalarConverter</converter>
     *   </UUID>
     * </converters>
     * ```
     */
    @Parameter(name = "converters")
    private var converters: Map<String, ScalarConverter> = mutableMapOf()

    /**
     * Directory file containing GraphQL queries, defaults to `src/main/resources`. Instead of specifying a directory you can
     * also specify list of query file by using [queryFiles] property instead.
     */
    @Parameter(defaultValue = "\${project.basedir}/src/main/resources", name = "queryFileDirectory")
    private lateinit var queryFileDirectory: File

    /**
     * List of query files to be processed. Instead of a list of files to be processed you can also specify [queryFileDirectory] directory
     * containing all the files. If this property is specified it will take precedence over the corresponding directory property.
     */
    @Parameter(name = "queryFiles")
    private lateinit var queryFiles: List<File>

    /**
     * Boolean flag indicating whether generated GraphQL client should be added to main or test sources.
     */
    @Parameter(defaultValue = "\${graphql.generateTestSources}", name = "generateTestSources")
    private var generateTestSources: Boolean = false

    /**
     * Custom target directory that will be used to store generated files.
     */
    @Parameter(name = "outputDirectory")
    private var outputDirectory: File? = null

    private fun getOutputDirectory(): File {
        val customDirectory = outputDirectory
        if (customDirectory != null) {
            return customDirectory
        }

        return if (generateTestSources) {
            File("${project.build.directory}/test-generated-sources/graphql")
        } else {
            File("${project.build.directory}/generated-sources/graphql")
        }
    }

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
            else -> queryFileDirectory.listFiles { file -> file.extension == "graphql" }?.toList() ?: throw RuntimeException("exception while looking up the query files")
        }
        if (targetQueryFiles.isEmpty()) {
            throw RuntimeException("no query files specified")
        }

        val targetDirectory = getOutputDirectory()
        if (!targetDirectory.isDirectory && !targetDirectory.mkdirs()) {
            throw RuntimeException("failed to generate generated source directory")
        }

        logConfiguration(targetQueryFiles)
        val config = GraphQLClientGeneratorConfig(
            packageName = packageName,
            allowDeprecated = allowDeprecatedFields,
            scalarTypeToConverterMapping = converters.map { (key, value) -> key to ScalarConverterMapping(value.type, value.converter) }.toMap()
        )
        generateClient(config, graphQLSchema, targetQueryFiles).forEach {
            it.writeTo(targetDirectory)
        }

        if (generateTestSources) {
            project.addTestCompileSourceRoot(targetDirectory.path)
        } else {
            project.addCompileSourceRoot(targetDirectory.path)
        }
        log.debug("successfully generated GraphQL HTTP client")
    }

    private fun logConfiguration(queryFiles: List<File>) {
        log.debug("GraphQL Client generator configuration:")
        log.debug("  schema file = ${schemaFile.path}")
        log.debug("  queries")
        queryFiles.forEach {
            log.debug("    - ${it.name}")
        }
        log.debug("  packageName = $packageName")
        log.debug("  allowDeprecatedFields = $allowDeprecatedFields")
        log.debug("  converters")
        converters.entries.forEach { (customScalar, converterInfo) ->
            log.debug("    - custom scalar = $customScalar")
            log.debug("      |- type = ${converterInfo.type}")
            log.debug("      |- converter = ${converterInfo.converter}")
        }
        log.debug("")
        log.debug("-- end GraphQL Client generator configuration --")
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
class ScalarConverter {
    /** Fully qualified class name of a custom scalar type, e.g. java.util.UUID */
    @Parameter
    lateinit var type: String

    /** Fully qualified class name of a custom converter used to convert to/from raw JSON and [type] */
    @Parameter
    lateinit var converter: String
}
