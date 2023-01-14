import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner
import java.time.Duration

description = "GraphQL Kotlin Maven Plugin that can generate type-safe GraphQL Kotlin client and GraphQL schema in SDL format using reflections"

buildscript {
    repositories {
        mavenCentral()
        mavenLocal {
            content {
                includeGroup("de.benediktritter")
            }
        }
    }

    dependencies {
        classpath(libs.wiremock.standalone)
        classpath("de.benediktritter:plugin:99.99.99")
    }
}

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.graphql.conventions")
//    alias(libs.plugins.maven.plugin.development)
}

apply(plugin = "de.benediktritter.maven-plugin-development")

dependencies {
    api(projects.graphqlKotlinClientGenerator)
    api(projects.graphqlKotlinSdlGenerator)
    api(libs.kotlinx.coroutines.core)
    implementation(libs.maven.plugin.annotations)
    implementation(libs.maven.plugin.api)
    implementation(libs.maven.project)
    testImplementation(projects.graphqlKotlinSpringServer)
    testImplementation(projects.graphqlKotlinFederatedHooksProvider)
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

    val kotlinJvmVersion: String by project
    /*
    Integration tests are run through maven-invoker-plugin which will execute tests under src/integration/<scenario>
     */
    val mavenEnvironmentVariables = mapOf(
        "graphqlKotlinVersion" to project.version,
        "graphqlJavaVersion" to libs.versions.graphql.java.get(),
        "kotlinJvmTarget" to kotlinJvmVersion,
        "kotlinVersion" to libs.versions.kotlin.get(),
        "kotlinxCoroutinesVersion" to libs.versions.kotlinx.coroutines.get(),
        "kotlinPoetVersion" to libs.versions.poet.get(),
        "kotlinxSerializationVersion" to libs.versions.kotlinx.serialization.get(),
        "ktorVersion" to libs.versions.ktor.get(),
        "reactorVersion" to libs.versions.reactor.core.get(),
        "junitVersion" to libs.versions.junit.get()
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
