package com.expediagroup.graphql.plugin.maven

import com.expediagroup.graphql.plugin.runIntrospectionQuery
import kotlinx.coroutines.runBlocking
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

@Mojo(name = "introspectSchema")
class IntrospectSchemaMojo : AbstractMojo() {

    @Parameter(readonly = true, defaultValue = "\${project}")
    private lateinit var project: MavenProject

    @Parameter(name = "endpoint", required = true)
    private lateinit var endpoint: String

    @Parameter(name = "outputFileName", defaultValue = "schema.graphql")
    private lateinit var outputFileName: String

    @Parameter(readonly = true, defaultValue = "\${project.build.directory}")
    private lateinit var outputDirectory: File

    @Suppress("EXPERIMENTAL_API_USAGE")
    override fun execute() {
        log.debug("executing introspectSchema MOJO against $endpoint")

        val schemaFile = File("${outputDirectory.absolutePath}/$outputFileName")
        runBlocking {
            val schema = runIntrospectionQuery(endpoint = endpoint)
            schemaFile.writeText(schema)
        }
    }
}
