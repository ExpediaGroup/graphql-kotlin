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

package com.expediagroup.graphql.plugin.gradle

import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.ContainsPattern
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import java.io.BufferedReader
import java.io.File
import java.io.StringWriter

abstract class GraphQLGradlePluginAbstractIT {

    // unsure if there is a better way - correct values are set from Gradle build
    // when running directly from IDE you will need to manually update those to correct values
    private val kotlinVersion = System.getProperty("kotlinVersion") ?: "1.7.20"
    private val junitVersion = System.getProperty("junitVersion") ?: "5.7.2"
    private val springBootVersion = System.getProperty("springBootVersion") ?: "2.5.5"

    val testSchema = loadResource("mocks/schema.graphql")
    val introspectionResult = loadResource("mocks/IntrospectionResult.json")
    val testQuery = loadResource("mocks/JUnitQuery.graphql")
    val testResponse = loadResource("mocks/JUnitQueryResponse.json")

    @BeforeEach
    fun setUp() {
        WireMock.reset()
        WireMock.stubFor(stubSdlEndpoint())
        WireMock.stubFor(stubIntrospectionResult())
        WireMock.stubFor(stubGraphQLResponse())
    }

    fun stubSdlEndpoint(delay: Int? = null): MappingBuilder = WireMock.get("/sdl")
        .withResponse(content = testSchema, contentType = "text/plain", delay = delay)

    fun stubIntrospectionResult(delay: Int? = null): MappingBuilder = WireMock.post("/graphql")
        .withRequestBody(ContainsPattern("IntrospectionQuery"))
        .withResponse(content = introspectionResult, delay = delay)

    fun stubGraphQLResponse(delay: Int? = null): MappingBuilder = WireMock.post("/graphql")
        .withRequestBody(ContainsPattern("JUnitQuery"))
        .withResponse(content = testResponse, delay = delay)

    private fun MappingBuilder.withResponse(content: String, contentType: String = "application/json", delay: Int? = null) = this.willReturn(
        WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", contentType)
            .withBody(content)
            .withFixedDelay(delay ?: 0)
    )

    fun loadResource(resourceName: String) = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName)?.use {
        BufferedReader(it.reader()).readText()
    } ?: throw RuntimeException("unable to load $resourceName")

    fun loadTemplate(templateName: String, configuration: Map<String, Any> = emptyMap()): String {
        val testApplicationMustache = mustacheFactory.compile("templates/$templateName.mustache")
        return testApplicationMustache.execute(StringWriter(), configuration).toString()
    }

    internal fun File.generateBuildFileForClient(
        contents: String,
        graphQLClientDependency: String = "implementation(\"com.expediagroup:graphql-kotlin-spring-client:$DEFAULT_PLUGIN_VERSION\")",
        serializer: GraphQLSerializer = GraphQLSerializer.JACKSON
    ) {
        val kotlinxSerializerPlugin = if (serializer == GraphQLSerializer.KOTLINX) {
            """kotlin("plugin.serialization") version "$kotlinVersion""""
        } else {
            ""
        }

        val plugins =
            """
            |plugins {
            |  kotlin("jvm") version "$kotlinVersion"
            |  $kotlinxSerializerPlugin
            |  id("com.expediagroup.graphql")
            |  application
            |}
            """.trimMargin()
        val dependencies =
            """
            |dependencies {
            |  implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
            |  $graphQLClientDependency
            |  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
            |  testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
            |}
            """.trimMargin()
        this.generateBuildFile(plugins, dependencies, contents)
    }

    internal fun File.generateBuildFileForServer(contents: String, additionalDependencies: String = "") {
        val plugins =
            """
            |plugins {
            |    kotlin("jvm") version "$kotlinVersion"
            |    kotlin("plugin.spring") version "$kotlinVersion"
            |    id("org.springframework.boot") version "$springBootVersion"
            |    id("com.expediagroup.graphql")
            |}
            """.trimMargin()
        val dependencies =
            """
            |dependencies {
            |    implementation("org.jetbrains.kotlin:kotlin-stdlib")
            |    implementation("com.expediagroup", "graphql-kotlin-spring-server", "$DEFAULT_PLUGIN_VERSION")
            |    implementation("com.expediagroup", "graphql-kotlin-hooks-provider", "$DEFAULT_PLUGIN_VERSION")
            |    $additionalDependencies
            |}
            """.trimMargin()
        this.generateBuildFile(plugins, dependencies, contents)
    }

    private fun File.generateBuildFile(plugins: String, dependencies: String, contents: String) {
        val buildFileContents =
            """
            |import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
            |import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
            |import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration
            |import com.expediagroup.graphql.plugin.gradle.graphql
            |import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
            |import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
            |import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask
            |import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask
            |
            |$plugins
            |
            |repositories {
            |  mavenCentral()
            |  mavenLocal {
            |    content {
            |      includeGroup("com.expediagroup")
            |    }
            |  }
            |}
            |
            |tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            |  kotlinOptions {
            |    jvmTarget = "1.8"
            |  }
            |}
            |
            |$dependencies
            |
            |$contents
            """.trimMargin()

        val buildFile = File(this, "build.gradle.kts")
        buildFile.writeText(buildFileContents)
    }

    internal fun File.generateGroovyBuildFileForClient(
        contents: String,
        graphQLClientDependency: String = "implementation \"com.expediagroup:graphql-kotlin-spring-client:$DEFAULT_PLUGIN_VERSION\"",
        serializer: GraphQLSerializer = GraphQLSerializer.JACKSON
    ) {
        val kotlinxSerializerPlugin = if (serializer == GraphQLSerializer.KOTLINX) {
            """id 'org.jetbrains.kotlin.plugin.serialization' version '$kotlinVersion'"""
        } else {
            ""
        }

        val plugins =
            """
            |plugins {
            |  id 'org.jetbrains.kotlin.jvm' version '$kotlinVersion'
            |  $kotlinxSerializerPlugin
            |  id 'com.expediagroup.graphql'
            |  id 'application'
            |}
            """.trimMargin()
        val dependencies =
            """
            |dependencies {
            |    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
            |    $graphQLClientDependency
            |    testImplementation "org.junit.jupiter:junit-jupiter-api:$junitVersion"
            |    testImplementation "org.junit.jupiter:junit-jupiter-engine:$junitVersion"
            |}
            """.trimMargin()
        return this.generateGroovyBuildFile(plugins, dependencies, contents)
    }

    internal fun File.generateGroovyBuildFileForServer(contents: String, additionalDependencies: String = "") {
        val plugins =
            """
            |plugins {
            |  id 'org.jetbrains.kotlin.jvm' version '$kotlinVersion'
            |  id 'org.jetbrains.kotlin.plugin.spring' version '$kotlinVersion'
            |  id 'org.springframework.boot' version '$springBootVersion'
            |  id 'com.expediagroup.graphql'
            |  id 'application'
            |}
            """.trimMargin()
        val dependencies =
            """
            |dependencies {
            |    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
            |    implementation "com.expediagroup:graphql-kotlin-spring-server:$DEFAULT_PLUGIN_VERSION"
            |    implementation "com.expediagroup:graphql-kotlin-hooks-provider:$DEFAULT_PLUGIN_VERSION"
            |    $additionalDependencies
            |}
            """.trimMargin()
        return this.generateGroovyBuildFile(plugins, dependencies, contents)
    }

    private fun File.generateGroovyBuildFile(plugins: String, dependencies: String, contents: String) {
        val buildFileContents =
            """
            |import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
            |import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
            |import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration
            |import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
            |import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
            |import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask
            |import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask
            |
            |$plugins
            |
            |repositories {
            |    mavenCentral()
            |    mavenLocal {
            |        content {
            |            includeGroup "com.expediagroup"
            |        }
            |    }
            |}
            |
            |compileKotlin {
            |    kotlinOptions.jvmTarget = "1.8"
            |}
            |
            |$dependencies
            |
            |$contents
            """.trimMargin()

        val buildFile = File(this, "build.gradle")
        buildFile.writeText(buildFileContents)
    }

    internal fun File.createTestFile(fileName: String, subDirectory: String? = null): File {
        val targetDirectory = if (subDirectory != null) {
            File(this, subDirectory)
        } else {
            this
        }
        targetDirectory.mkdirs()
        return File(targetDirectory, fileName)
    }

    companion object {
        internal val wireMockServer: WireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())
        internal val mustacheFactory: MustacheFactory = DefaultMustacheFactory()

        @BeforeAll
        @JvmStatic
        fun oneTimeSetup() {
            wireMockServer.start()
            WireMock.configureFor(wireMockServer.port())
        }

        @AfterAll
        @JvmStatic
        fun oneTimeTearDown() {
            wireMockServer.stop()
        }
    }
}
