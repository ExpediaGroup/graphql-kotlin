import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner
import java.time.Duration

description = "Gradle Kotlin Maven Plugin that can generate type-safe GraphQL Kotlin client and GraphQL schema in SDL format using reflections"

val graphQLJavaVersion: String by project
val junitVersion: String by project
val kotlinJvmVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project
val kotlinPoetVersion: String by project
val ktorVersion: String by project

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
    id("de.benediktritter.maven-plugin-development") version "0.2.0"
}

dependencies {
    api(project(path = ":graphql-kotlin-plugin-core"))
    api(project(path = ":graphql-kotlin-sdl-generator"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
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
        "kotlinCoroutinesVersion" to kotlinCoroutinesVersion,
        "kotlinPoetVersion" to kotlinPoetVersion,
        "ktorVersion" to ktorVersion,
        "junitVersion" to junitVersion
    )
    var wireMockServer: WireMockServerRunner? = null
    var wireMockServerPort: Int? = null
    val startWireMock by register("startWireMock") {
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
        finalizedBy("stopWireMock")
    }
    val stopWireMock by register("stopWireMock") {
        doLast {
            val server = wireMockServer
            if (server?.isRunning == true) {
                logger.info("attempting to stop wiremock server")
                server.stop()
                logger.info("wiremock server stopped")
            }
        }
        mustRunAfter("startWireMock")
    }
    val integrationTest by register("integrationTest") {
        // since we will be running maven we need to explicitly specify that integration test
        // should run after artifacts are published to local m2 repo
        dependsOn(":graphql-kotlin-types:publishToMavenLocal")
        dependsOn(":graphql-kotlin-client:publishToMavenLocal")
        dependsOn(":graphql-kotlin-ktor-client:publishToMavenLocal")
        dependsOn(":graphql-kotlin-spring-client:publishToMavenLocal")
        dependsOn(":graphql-kotlin-spring-server:publishToMavenLocal")
        dependsOn(":graphql-kotlin-plugin-core:publishToMavenLocal")
        dependsOn(":graphql-kotlin-sdl-generator:publishToMavenLocal")
        dependsOn(":graphql-kotlin-hooks-provider:publishToMavenLocal")
        dependsOn(":graphql-kotlin-federated-hooks-provider:publishToMavenLocal")
        dependsOn("publishToMavenLocal")
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
