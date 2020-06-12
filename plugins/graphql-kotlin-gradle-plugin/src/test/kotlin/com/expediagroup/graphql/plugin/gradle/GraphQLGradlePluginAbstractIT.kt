/*
 * Copyright 2020 Expedia, Inc
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
    private val gqlKotlinVersion = System.getProperty("graphQLKotlinVersion") ?: "3.0.0-SNAPSHOT"
    private val kotlinVersion = System.getProperty("kotlinVersion") ?: "1.3.71"
    private val junitVersion = System.getProperty("junitVersion") ?: "5.6.0"

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

    fun stubSdlEndpoint(): MappingBuilder = WireMock.get("/sdl")
        .withResponse(content = testSchema, contentType = "text/plain")

    fun stubIntrospectionResult(): MappingBuilder = WireMock.post("/graphql")
        .withRequestBody(ContainsPattern("IntrospectionQuery"))
        .withResponse(content = introspectionResult)

    fun stubGraphQLResponse(): MappingBuilder = WireMock.post("/graphql")
        .withRequestBody(ContainsPattern("JUnitQuery"))
        .withResponse(content = testResponse)

    private fun MappingBuilder.withResponse(content: String, contentType: String = "application/json") = this.willReturn(
        WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", contentType)
            .withBody(content)
    )

    fun loadResource(resourceName: String) = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName)?.use {
        BufferedReader(it.reader()).readText()
    } ?: throw RuntimeException("unable to load $resourceName")

    fun loadTemplate(templateName: String, configuration: Map<String, Any>): String {
        val testApplicationMustache = mustacheFactory.compile("templates/$templateName.mustache")
        return testApplicationMustache.execute(StringWriter(), configuration).toString()
    }

    internal fun File.generateBuildFile(contents: String) {
        val buildFileContents =
            """
            import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
            import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
            import com.expediagroup.graphql.plugin.gradle.graphql
            import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
            import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
            import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask

            plugins {
              id("org.jetbrains.kotlin.jvm") version "$kotlinVersion"
              id("com.expediagroup.graphql")
              application
            }

            repositories {
                mavenCentral()
                mavenLocal()
            }

            tasks.withType<KotlinCompile> {
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
                implementation("com.expediagroup:graphql-kotlin-client:$gqlKotlinVersion")
                testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
                testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
            }

            $contents
            """.trimIndent()

        val buildFile = File(this, "build.gradle.kts")
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
