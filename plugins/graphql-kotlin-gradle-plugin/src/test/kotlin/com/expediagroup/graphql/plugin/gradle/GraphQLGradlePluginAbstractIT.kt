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

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.ContainsPattern
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import java.io.BufferedReader
import java.io.File

abstract class GraphQLGradlePluginAbstractIT {

    // unsure if there is a better way - correct values are set from Gradle build
    // when running directly from IDE you will need to manually update those to correct values
    private val gqlKotlinVersion = System.getProperty("graphQLKotlinVersion") ?: "3.0.0-SNAPSHOT"
    private val kotlinVersion = System.getProperty("kotlinVersion") ?: "1.3.71"
    private val junitVersion = System.getProperty("junitVersion") ?: "5.6.0"

    val testSchema = loadResource("testSchema.graphql")
    private val introspectionResult = loadResource("introspectionResult.json")
    private val testResponse = loadResource("testResponse.json")

    @BeforeEach
    fun setUp() {
        WireMock.reset()
        WireMock.stubFor(WireMock.get("/sdl")
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/plain")
                .withBody(testSchema)))
        WireMock.stubFor(WireMock.post("/graphql")
            .withRequestBody(ContainsPattern("IntrospectionQuery"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(introspectionResult)))
        WireMock.stubFor(WireMock.post("/graphql")
            .withRequestBody(ContainsPattern("JUnitQuery"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(testResponse)))
    }

    fun loadResource(resourceName: String) = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName)?.use {
        BufferedReader(it.reader()).readText()
    } ?: throw RuntimeException("unable to load $resourceName")

    fun generateDefaultBuildFile(tempDir: File): File {
        val buildFileContents = """
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

        """.trimIndent()

        val buildFile = File(tempDir, "build.gradle.kts")
        buildFile.writeText(buildFileContents)
        return buildFile
    }

    companion object {
        internal val wireMockServer: WireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())

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
