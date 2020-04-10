package com.expediagroup.graphql.plugin.maven

import com.expediagroup.graphql.plugin.generateClient
import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorConfig
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

@Mojo(name = "generateClient")
class GenerateClientMojo : AbstractMojo() {

    @Parameter(readonly = true, defaultValue = "\${project}")
    private lateinit var project: MavenProject

    @Parameter(name = "schemaFile")
    private lateinit var schemaFile: File

    @Parameter(name = "packageName", required = true)
    private lateinit var packageName: String

    @Parameter(name = "converters")
    private lateinit var scalarConverters: Map<String, ScalarConverterMapping>

    @Parameter(name = "queryFileDirectory", defaultValue = "\${project.basedir}/src/main/resources")
    private lateinit var queryFileDirectory: File

    @Parameter(name = "queryFiles")
    private lateinit var queryFiles: List<File>

    @Parameter(name = "outputDirectory", defaultValue = "\${project.build.directory}/generated/sources/graphql")
    private lateinit var outputDirectory: File

    @Suppress("EXPERIMENTAL_API_USAGE")
    override fun execute() {
        log.debug("generating GraphQL client")
        val graphQLSchema = if (::schemaFile.isInitialized) {
            schemaFile
        } else {
            throw RuntimeException("schema not available")
        }

        val targetQueryFiles: List<File> = when {
            ::queryFiles.isInitialized -> queryFiles
            ::queryFileDirectory.isInitialized -> queryFileDirectory
                .listFiles { file -> file.extension == "graphql" }
                ?.toList() ?: throw RuntimeException("exception while looking up the query files")
            else -> throw RuntimeException("no query files found")
        }

        val targetDirectoryPath = packageName.replace('.', File.separatorChar)
        val targetDirectory = File(outputDirectory, targetDirectoryPath)
        if (!targetDirectory.mkdirs()) {
            throw RuntimeException("failed to generate generated source directory")
        }
        val config = GraphQLClientGeneratorConfig(packageName = packageName)
        generateClient(config, graphQLSchema, targetQueryFiles).forEach {
            it.writeTo(targetDirectory)
        }
    }
}
