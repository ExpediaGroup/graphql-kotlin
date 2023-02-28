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
package com.expediagroup.graphql.examples.server.ktor

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.examples.server.ktor.schema.BookQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.CourseQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.HelloQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.LoginMutationService
import com.expediagroup.graphql.examples.server.ktor.schema.UniversityQueryService
import com.expediagroup.graphql.examples.server.ktor.schema.dataloaders.BookDataLoader
import com.expediagroup.graphql.examples.server.ktor.schema.dataloaders.CourseDataLoader
import com.expediagroup.graphql.examples.server.ktor.schema.dataloaders.UniversityDataLoader
import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.graphQLGetRoute
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphQLSDLRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.Routing

fun Application.graphQLModule() {
    install(GraphQL) {
        schema {
            packages = listOf("com.expediagroup.graphql.examples.server")
            queries = listOf(
                HelloQueryService(),
                BookQueryService(),
                CourseQueryService(),
                UniversityQueryService(),
            )
            mutations = listOf(
                LoginMutationService()
            )
        }
        engine {
            dataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory(
                UniversityDataLoader, CourseDataLoader, BookDataLoader
            )
        }
        server {
            contextFactory = CustomGraphQLContextFactory()
        }
    }
    install(Routing) {
        graphQLGetRoute()
        graphQLPostRoute()
        graphiQLRoute()
        graphQLSDLRoute()
    }
}
