import java.time.Duration

description = "GraphQL Kotlin Maven plugin."

val mavenPluginApiVersion: String = "3.6.3"
val mavenPluginAnnotationVersion: String = "3.4"
val mavenProjectVersion: String = "2.2.1"
val mavenPluginVersion: String = "3.6.0"

dependencies {
    api(project(path = ":plugins:graphql-kotlin-plugin-core"))
    implementation("org.apache.maven:maven-plugin-api:$mavenPluginApiVersion")
    implementation("org.apache.maven:maven-project:$mavenProjectVersion")
    implementation("org.apache.maven.plugin-tools:maven-plugin-annotations:$mavenPluginAnnotationVersion")
}

tasks {
    publishing {
        publications {
            val mavenPublication = findByName("mavenJava") as? MavenPublication
            mavenPublication?.pom {
                packaging = "maven-plugin"
            }
        }
    }

    /*
    Maven plugins require plugin.xml descriptor which can be automatically generated using maven-plugin-plugin.

    This is a workaround to generate descriptor automatically:
    1) copy graphql-kotlin libraries to build directory so they can be referenced by maven build
    2) run maven wrapper to execute maven-plugin-plugin descriptor MOJO
    3) add generated descriptor XMLs to the generated JAR
     */
    val copyDependencies by register<Copy>("copyDependencies") {
        from(configurations.runtimeClasspath) {
            // we only need to explicitly copy graphql-kotlin libraries as they won't be available in m2 repository at this point
            // we could copy other direct dependencies as well but it looks like it might not work for maven-plugin-plugin as it relies on some transitive dependencies to run
            include("graphql-kotlin*")
        }
        into("${project.buildDir}/dependencies")
    }
    val mavenPluginDescriptor by register("mavenPluginDescriptor") {
        dependsOn("copyDependencies")
        timeout.set(Duration.ofSeconds(60))
        doLast {
            exec {
                val kotlinVersion: String by project
                val kotlinCoroutinesVersion: String by project
                environment("graphql-kotlin.version", project.version)
                environment("kotlin.version", kotlinVersion)
                environment("kotlin-coroutines.version", kotlinCoroutinesVersion)
                environment("maven-project.version", mavenProjectVersion)
                environment("maven-plugin-api.version", mavenPluginApiVersion)
                environment("maven-plugin-annotations.version", mavenPluginAnnotationVersion)
                environment("maven-plugin-plugin.version", mavenPluginVersion)

                commandLine("${project.projectDir}/mvnw", "plugin:descriptor")
            }
        }
    }
    jar {
        // explicitly copy generated plugin descriptors
        // there is a bug in maven-plugin-plugin that ignores custom output directory for one of the generated descriptors
        // see https://issues.apache.org/jira/browse/MPLUGIN-360
        dependsOn(mavenPluginDescriptor.path)
        from("${project.buildDir}/META-INF") {
            into("META-INF")
        }
    }
}
