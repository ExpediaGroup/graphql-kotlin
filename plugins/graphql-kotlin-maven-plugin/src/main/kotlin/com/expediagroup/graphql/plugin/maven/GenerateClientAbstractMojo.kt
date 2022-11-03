/*
 * Copyright 2021 Expedia, Inc
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

import com.expediagroup.graphql.plugin.client.generateClient
import com.expediagroup.graphql.plugin.client.generator.GraphQLScalar
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

/**
 * Generate GraphQL client code based on the provided GraphQL schema and target queries.
 */
abstract class GenerateClientAbstractMojo : AbstractMojo() {

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private lateinit var project: MavenProject

    /**
     * GraphQL schema file that will be used to generate client code.
     */
    @Parameter(defaultValue = "\${graphql.schemaFile}", name = "schemaFile")
    private var schemaFile: String? = null

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
     * List of custom GraphQL scalar converters.
     *
     * ```xml
     * <customScalars>
     *     <customScalar>
     *         <!-- custom scalar UUID type -->
     *         <scalar>UUID</scalar>
     *         <!-- fully qualified Java class name of a custom scalar type -->
     *         <type>java.util.UUID</type>
     *         <!-- fully qualified Java class name of a custom com.expediagroup.graphql.client.converter.ScalarConverter
     *            used to convert to/from raw JSON and scalar type -->
     *         <converter>com.example.UUIDScalarConverter</converter>
     *     </customScalar>
     * </customScalars>
     * ```
     */
    @Parameter(name = "customScalars")
    private var customScalars: List<CustomScalar> = mutableListOf()

    /**
     * Configure options for parsing GraphQL queries and schema definition language documents. Settings
     * here override the defaults set by GraphQL Java.
     *
     * ```xml
     * <parserOptions>
     *     <maxTokens>15000</maxTokens>
     *     <maxWhitespaceTokens>200000</maxWhitespaceTokens>
     *     <captureIgnoredChars>false</captureIgnoredChars>
     *     <captureLineComments>false</captureLineComments>
     *     <captureSourceLocation>true</captureSourceLocation>
     * </parserOptions>
     * ```
     */
    @Parameter(name = "parserOptions")
    private var parserOptions: ParserOptions? = null

    /**
     * Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using
     * [queryFiles] property instead.
     */
    abstract var queryFileDirectory: File

    /**
     * List of query files to be processed. Instead of a list of files to be processed you can also specify [queryFileDirectory] directory
     * containing all the files. If this property is specified it will take precedence over the corresponding directory property.
     */
    @Parameter(name = "queryFiles")
    private var queryFiles: List<File>? = null

    /**
     * JSON serializer that will be used to generate the data classes..
     */
    @Parameter(name = "serializer")
    private var serializer: GraphQLSerializer = GraphQLSerializer.JACKSON

    /**
     * Explicit opt-in flag to wrap nullable arguments in OptionalInput that supports both null and undefined values.
     */
    @Parameter(defaultValue = "\${graphql.useOptionalInputWrapper}", name = "useOptionalInputWrapper")
    private var useOptionalInputWrapper: Boolean = false

    /**
     * Target directory where to store generated files.
     */
    abstract var outputDirectory: File

    override fun execute() {
        log.debug("generating GraphQL client")

        val schemaPath = schemaFile ?: File(project.build.directory, "schema.graphql").path

        val targetQueryFiles: List<File> = locateQueryFiles(queryFiles, queryFileDirectory)

        if (!outputDirectory.isDirectory && !outputDirectory.mkdirs()) {
            throw RuntimeException("failed to generate generated source directory")
        }

        logConfiguration(schemaPath, targetQueryFiles)
        val customGraphQLScalars = customScalars.map { GraphQLScalar(it.scalar, it.type, it.converter) }
        generateClient(packageName, allowDeprecatedFields, customGraphQLScalars, serializer, schemaPath, targetQueryFiles, useOptionalInputWrapper, parserOptions = {
            parserOptions?.apply {
                maxTokens?.let { maxTokens(it) }
                maxWhitespaceTokens?.let { maxWhitespaceTokens(it) }
                captureIgnoredChars?.let { captureIgnoredChars(it) }
                captureLineComments?.let { captureLineComments(it) }
                captureSourceLocation?.let { captureSourceLocation(it) }
            }
        }).forEach {
            it.writeTo(outputDirectory)
        }

        configureProjectWithGeneratedSources(project, outputDirectory)
        log.debug("successfully generated GraphQL HTTP client")
    }

    private fun locateQueryFiles(files: List<File>?, directory: File): List<File> {
        val targetQueryFiles: List<File> = files ?: directory.listFiles { file -> file.extension == "graphql" }?.toList() ?: throw RuntimeException("exception while looking up the query files")
        if (targetQueryFiles.isEmpty()) {
            throw RuntimeException("no query files specified")
        }
        return targetQueryFiles
    }

    abstract fun configureProjectWithGeneratedSources(mavenProject: MavenProject, generatedSourcesDirectory: File)

    private fun logConfiguration(graphQLSchemaFilePath: String, queryFiles: List<File>) {
        log.debug("GraphQL Client generator configuration:")
        log.debug("  schema file = $graphQLSchemaFilePath")
        log.debug("  queries")
        queryFiles.forEach {
            log.debug("    - ${it.name}")
        }
        log.debug("  packageName = $packageName")
        log.debug("  allowDeprecatedFields = $allowDeprecatedFields")
        log.debug("  converters")
        customScalars.forEach { converterInfo ->
            log.debug("    - custom scalar = ${converterInfo.scalar}")
            log.debug("      |- kotlin type = ${converterInfo.type}")
            log.debug("      |- converter = ${converterInfo.converter}")
        }
        parserOptions?.apply {
            log.debug("  parserOptions")
            maxTokens?.let { log.debug("    maxTokens = $it") }
            maxWhitespaceTokens?.let { log.debug("    maxWhitespaceTokens = $it") }
            captureIgnoredChars?.let { log.debug("    captureIgnoredChars = $it") }
            captureLineComments?.let { log.debug("    captureLineComments = $it") }
            captureSourceLocation?.let { log.debug("    captureSourceLocation = $it") }
        }
        log.debug("")
        log.debug("-- end GraphQL Client generator configuration --")
    }
}

/**
 * Holds mapping between custom GraphQL scalar type, corresponding Kotlin type and the converter that will be used to convert to/from
 * raw JSON and Java type.
 *
 * Unfortunately we cannot use client-generator GraphQLScalar directly as per rules of mapping complex objects to Mojo parameters, target
 * object has to be declared in the same package as Mojo itself (otherwise we need to explicitly specify fully qualified implementation
 * name in configuration XML block).
 *
 * @see [Guide to Configuring Plug-ins](https://maven.apache.org/guides/mini/guide-configuring-plugins.html#Mapping_Complex_Objects)
 */
class CustomScalar {
    /** Custom scalar name. */
    @Parameter
    lateinit var scalar: String

    /** Fully qualified class name of a custom scalar type, e.g. java.util.UUID */
    @Parameter
    lateinit var type: String

    /** Fully qualified class name of a custom converter used to convert to/from raw JSON and [type] */
    @Parameter
    lateinit var converter: String
}

/**
 * Configure options for parsing GraphQL queries and schema definition language documents. Settings
 * here override the defaults set by GraphQL Java.
 */
class ParserOptions {
    /** Modify the maximum number of tokens read to prevent processing extremely large queries */
    @Parameter
    var maxTokens: Int? = null

    /** Modify the maximum number of whitespace tokens read to prevent processing extremely large queries */
    @Parameter
    var maxWhitespaceTokens: Int? = null

    /** Memory usage is significantly reduced by not capturing ignored characters, especially in SDL parsing. */
    @Parameter
    var captureIgnoredChars: Boolean? = null

    /** Single-line comments do not have any semantic meaning in GraphQL source documents and can be ignored */
    @Parameter
    var captureLineComments: Boolean? = null

    /** Memory usage is reduced by not setting SourceLocations on AST nodes, especially in SDL parsing. */
    @Parameter
    var captureSourceLocation: Boolean? = null
}
