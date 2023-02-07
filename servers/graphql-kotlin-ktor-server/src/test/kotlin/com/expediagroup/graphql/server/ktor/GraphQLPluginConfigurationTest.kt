/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.server.ktor

import com.expediagroup.graphql.generator.exceptions.EmptyQueryTypeException
import com.expediagroup.graphql.server.operations.Query
import graphql.execution.preparsed.NoOpPreparsedDocumentProvider
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class GraphQLPluginConfigurationTest {

    @Test
    fun `verify exception will be thrown if packages properties is missing`() {
        assertFailsWith<IllegalStateException> {
            embeddedServer(CIO, port = 0, module = Application::missingPackageGraphQLModule).start(wait = true)
        }
    }

    @Test
    fun `verify exception will be thrown if no queries are specified`() {
        assertFailsWith<EmptyQueryTypeException> {
            embeddedServer(CIO, port = 0, module = Application::missingQueriesGraphQLModule).start(wait = true)
        }
    }

    @Test
    fun `verify exception will be thrown when generating federated schema without hooks`() {
        assertFailsWith<IllegalStateException> {
            embeddedServer(CIO, port = 0, module = Application::missingFederatedHooksGraphQLModule).start(wait = true)
        }
    }

    @Test
    fun `verify exception will be thrown if preparsed document provider and APQs are configured`() {
        assertFailsWith<IllegalStateException> {
            embeddedServer(CIO, port = 0, module = Application::misconfiguredAPQGraphQLModule).start(wait = true)
        }
    }
}

class ConfigurationTestQuery : Query {
    fun foo(): String = TODO()
}

fun Application.missingPackageGraphQLModule() {
    install(GraphQL) {
        schema {
            queries = listOf(
                ConfigurationTestQuery(),
            )
        }
    }
}

fun Application.missingQueriesGraphQLModule() {
    install(GraphQL) {
        schema {
            packages = listOf("com.expediagroup.graphql.server.ktor")
        }
    }
}

fun Application.missingFederatedHooksGraphQLModule() {
    install(GraphQL) {
        schema {
            packages = listOf("com.expediagroup.graphql.server.ktor")
            queries = listOf(
                ConfigurationTestQuery(),
            )
            federation {
                enabled = true
                tracing {
                    enabled = true
                    debug = true
                }
            }
        }
    }
}

fun Application.misconfiguredAPQGraphQLModule() {
    install(GraphQL) {
        schema {
            packages = listOf("com.expediagroup.graphql.server.ktor")
            queries = listOf(
                ConfigurationTestQuery(),
            )
        }
        engine {
            preparsedDocumentProvider = NoOpPreparsedDocumentProvider()
            automaticPersistedQueries {
                enabled = true
            }
        }
    }
}
