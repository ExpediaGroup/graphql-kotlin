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

package com.expediagroup.graphql.examples.server.ktor

import com.expediagroup.graphql.examples.server.ktor.schema.models.User
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import io.ktor.request.ApplicationRequest

/**
 * Custom logic for how this example app should create its context given the [ApplicationRequest]
 */
class KtorGraphQLContextFactory : GraphQLContextFactory<AuthorizedContext, ApplicationRequest> {

    override fun generateContext(request: ApplicationRequest): AuthorizedContext {
        val loggedInUser = User(
            email = "fake@site.com",
            firstName = "Someone",
            lastName = "You Don't know",
            universityId = 4
        )

        // Parse any headers from the Ktor request
        val customHeader: String? = request.headers["my-custom-header"]

        return AuthorizedContext(loggedInUser, customHeader = customHeader)
    }
}
