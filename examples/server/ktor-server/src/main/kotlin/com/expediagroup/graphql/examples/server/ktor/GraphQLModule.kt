/*
 * Copyright 2022 Expedia, Inc
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

@file:Suppress("DEPRECATION")

package com.expediagroup.graphql.examples.server.ktor

import com.expediagroup.graphql.examples.server.ktor.schema.BookQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.CourseQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.HelloQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.LoginMutationService
import com.expediagroup.graphql.examples.server.ktor.schema.UniversityQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.models.User
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.scalars.IDValueUnboxer
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Application.graphQLModule() {
    install(Routing)
    install(ContentNegotiation) {
        jackson()
    }
    install(GraphQLKotlin) {
        enablePlayground = true
        queries = listOf(
            HelloQueryService(),
            BookQueryService(),
            CourseQueryService(),
            UniversityQueryService(),
        )
        mutations = listOf(
            LoginMutationService()
        )
        schemaGeneratorConfig = SchemaGeneratorConfig(
            supportedPackages = listOf(
                "com.expediagroup.graphql.examples.server.ktor"
            ),
        )
        configureGraphQL {
            valueUnboxer(IDValueUnboxer())
        }
        generateContextMap = { request: ApplicationRequest ->
            myGenerateContextMap(request)
        }
    }
    // TODO: this should be automatically called by the plugin. No global vars should be needed
    installEndpoints(KtorGraphQLConfig.config)
}

fun myGenerateContextMap(request: ApplicationRequest) = mapOf(
    User::class to User(
        email = "fake@site.com",
        firstName = "Someone",
        lastName = "You Don't know",
        universityId = 4
    ),
    "Header" to (request.headers["my-custom-header"] ?: "")
)

