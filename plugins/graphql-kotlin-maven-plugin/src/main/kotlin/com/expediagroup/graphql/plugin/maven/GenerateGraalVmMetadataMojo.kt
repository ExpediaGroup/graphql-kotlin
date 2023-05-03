package com.expediagroup.graphql.plugin.maven

import com.expediagroup.graphql.plugin.graalvm.generateGraalVmMetadata
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import java.io.File

/**
 * Generate GraalVM reachability metadata for GraphQL Kotlin servers.
 *
 * @see <a href="https://www.graalvm.org/latest/reference-manual/native-image/metadata">GraalVM Reachability Metadata</a>
 */
@Mojo(name = "generate-graalvm-metadata", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE)
class GenerateGraalVmMetadataMojo : AbstractSourceMojo() {

    /**
     * List of supported packages that can be scanned to generate schema.
     */
    @Parameter(required = true)
    private lateinit var packages: List<String>

    /**
     * Optional application main class name.
     */
    @Parameter
    private var mainClassName: String? = null

    /**
     * Target directory where to store generated files, defaults to `target`.
     */
    @Parameter(defaultValue = "\${project.build.outputDirectory}", name = "outputDirectory")
    override lateinit var outputDirectory: File

    override fun generate() {
        val metadataDirectory = File(outputDirectory, "META-INF/native-image/${project.groupId}/${project.name}/graphql")
        if (!metadataDirectory.isDirectory && !metadataDirectory.mkdirs()) {
            throw RuntimeException("failed to create reachability metadata directory")
        }
        log.debug("attempting to generate GraalVM using custom classloader")
        generateGraalVmMetadata(metadataDirectory, packages, mainClassName)
        log.debug("successfully generated GraalVM metadata")
    }
}
