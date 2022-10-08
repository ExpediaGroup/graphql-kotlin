import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner
import java.time.Duration

description = "GraphQL Kotlin Maven Plugin that can generate type-safe GraphQL Kotlin client and GraphQL schema in SDL format using reflections"

val graphQLJavaVersion: String by project
val junitVersion: String by project
val kotlinJvmVersion: String by project
val kotlinVersion: String by project
val kotlinxCoroutinesVersion: String by project
val kotlinPoetVersion: String by project
val kotlinxSerializationVersion: String by project
val ktorVersion: String by project
val reactorVersion: String by project

// maven dependencies
val mavenPluginApiVersion: String = "3.6.3"
val mavenPluginAnnotationVersion: String = "3.6.0"
val mavenProjectVersion: String = "2.2.1"

buildscript {
    // cannot access project at this time
    val wireMockVersion: String = "2.26.2"
    dependencies {
        classpath("com.github.tomakehurst:wiremock-jre8-standalone:$wireMockVersion")
    }
}

plugins {
    id("de.benediktritter.maven-plugin-development")
}

dependencies {
    api(project(path = ":graphql-kotlin-client-generator"))
    api(project(path = ":graphql-kotlin-sdl-generator"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.apache.maven:maven-plugin-api:$mavenPluginApiVersion")
    implementation("org.apache.maven:maven-project:$mavenProjectVersion")
    implementation("org.apache.maven.plugin-tools:maven-plugin-annotations:$mavenPluginAnnotationVersion")
    testImplementation(project(path = ":graphql-kotlin-spring-server"))
    testImplementation(project(path = ":graphql-kotlin-federated-hooks-provider"))
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
    Integration tests are run through maven-invoker-plugin which will execute tests under src/integration/<scenario>
     */
    val mavenEnvironmentVariables = mapOf(
        "graphqlKotlinVersion" to project.version,
        "graphqlJavaVersion" to graphQLJavaVersion,
        "kotlinJvmTarget" to kotlinJvmVersion,
        "kotlinVersion" to kotlinVersion,
        "kotlinxCoroutinesVersion" to kotlinxCoroutinesVersion,
        "kotlinPoetVersion" to kotlinPoetVersion,
        "kotlinxSerializationVersion" to kotlinxSerializationVersion,
        "ktorVersion" to ktorVersion,
        "reactorVersion" to reactorVersion,
        "junitVersion" to junitVersion
    )
    var wireMockServer: WireMockServerRunner? = null
    var wireMockServerPort: Int? = null
    val startWireMock by register("startWireMock") {
        dependsOn(":resolveIntegrationTestDependencies")
        finalizedBy("stopWireMock")

        doLast {
            val wireMockConfig = arrayOf(
                "--root-dir=${project.projectDir}/src/integration/wiremock",
                "--port=0"
            )
            wireMockServer = WireMockServerRunner()
            wireMockServer?.run(*wireMockConfig)
            wireMockServerPort = wireMockServer?.port()
            logger.info("wiremock started at port $wireMockServerPort")
        }
    }
    val stopWireMock by register("stopWireMock") {
        mustRunAfter("startWireMock")

        doLast {
            val server = wireMockServer
            if (server?.isRunning == true) {
                logger.info("attempting to stop wiremock server")
                server.stop()
                logger.info("wiremock server stopped")
            }
        }
    }
    val integrationTest by register("integrationTest") {
        dependsOn(startWireMock.path)
        finalizedBy(stopWireMock.path)
        timeout.set(Duration.ofSeconds(500))
        doLast {
            exec {
                environment(mavenEnvironmentVariables)
                environment("graphqlEndpoint", "http://localhost:$wireMockServerPort")
                commandLine("${project.projectDir}/mvnw", "dependency:go-offline", "invoker:install", "invoker:run", "--no-transfer-progress")
            }
        }
    }
    check {
        dependsOn(integrationTest.path)
    }
}
