import java.time.Duration
import kotlin.arrayOf
import kotlin.collections.listOf
import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner

description = "GraphQL Kotlin Maven plugin"

val graphQLJavaVersion: String by project
val junitVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project
val kotlinPoetVersion: String by project
val ktorVersion: String by project
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
    api(project(path = ":plugins:graphql-kotlin-plugin-core"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.apache.maven:maven-plugin-api:$mavenPluginApiVersion")
    implementation("org.apache.maven:maven-project:$mavenProjectVersion")
    implementation("org.apache.maven.plugin-tools:maven-plugin-annotations:$mavenPluginAnnotationVersion")
    testImplementation(project(path = ":graphql-kotlin-client"))
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

    Steps:
    1) copy dependent graphql-kotlin libraries to a local folder
    2) install all required graphql-kotlin libraries to a local m2 repo
      - we need to explicitly install libs as otherwise they won't be available to maven build at this point of time
      - alternative approach: make integration test dependent on publishToMavenLocal task from the dependent libs, con: publish task is dependent on dokka which
       only runs on Java 8 so we would need to have custom logic to handle that
    3) run integration tests using test project that executes maven-invoker-plugin
      - library versions are passed to the maven build which are then set by the maven-invoker-plugin when it is filtering the resources

    TODO update this to not be dependent on Maven
     */
    val copyIntegrationTestDependencies by register<Copy>("copyIntegrationTestDependencies") {
        // we only need to explicitly copy graphql-kotlin libraries as they won't be available in m2 repository at this point
        from(configurations.runtimeClasspath) {
            include("graphql-kotlin*")
        }
        from(configurations.testRuntimeClasspath) {
            include("graphql-kotlin*")
        }
        into("${project.buildDir}/dependencies")
    }
    val jar by getting(Jar::class)
    val mavenEnvironmentVariables = mapOf(
        "graphqlKotlinVersion" to project.version,
        "graphqlJavaVersion" to graphQLJavaVersion,
        "kotlinVersion" to kotlinVersion,
        "kotlinCoroutinesVersion" to kotlinCoroutinesVersion,
        "kotlinPoetVersion" to kotlinPoetVersion,
        "ktorVersion" to ktorVersion,
        "junitVersion" to junitVersion
    )
    val installIntegrationTestDependencies by register("installIntegrationTestDependencies") {
        dependsOn(copyIntegrationTestDependencies.path, jar.path)
        doLast {
            for (dependentJar in listOf("graphql-kotlin-plugin-core", "graphql-kotlin-client")) {
                exec {
                    val graphqlKotlinLibJar = "${project.buildDir}/dependencies/$dependentJar-${project.version}.jar"
                    environment(mavenEnvironmentVariables)
                    commandLine("${project.projectDir}/mvnw",
                        "install:install-file",
                        "-Dfile=$graphqlKotlinLibJar",
                        "-DgroupId=${project.group}",
                        "-DartifactId=$dependentJar",
                        "-Dversion=${project.version}",
                        "-Dpackaging=jar")
                }
            }
            exec {
                val pluginJar = jar.archiveFile.get().asFile.absolutePath
                environment(mavenEnvironmentVariables)
                commandLine("${project.projectDir}/mvnw",
                    "install:install-file",
                    "-Dfile=$pluginJar",
                    "-DgroupId=${project.group}",
                    "-DartifactId=${project.name}",
                    "-Dversion=${project.version}",
                    "-Dpackaging=maven-plugin")
            }
        }
    }

    var wireMockServer: WireMockServerRunner? = null
    var wireMockServerPort: Int? = null
    val startWireMock by register("startWireMock") {
        doLast {
            val wireMockConfig = arrayOf(
                "--root-dir=${project.projectDir}/src/integration/wiremock",
                "--port=0")
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
        dependsOn(installIntegrationTestDependencies.path, startWireMock.path)
        finalizedBy(stopWireMock.path)
        timeout.set(Duration.ofSeconds(500))
        doLast {
            exec {
                environment(mavenEnvironmentVariables)
                environment("graphqlEndpoint", "http://localhost:$wireMockServerPort")
                commandLine("${project.projectDir}/mvnw", "invoker:install", "invoker:run")
            }
        }
    }
    check {
        dependsOn(integrationTest.path)
    }
}
