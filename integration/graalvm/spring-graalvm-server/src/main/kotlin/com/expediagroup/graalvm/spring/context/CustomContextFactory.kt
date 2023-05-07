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

package com.expediagroup.graalvm.spring.context

import com.expediagroup.graphql.generator.extensions.plus
import com.expediagroup.graphql.server.spring.execution.DefaultSpringGraphQLContextFactory
import graphql.GraphQLContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import java.util.UUID

@Component
class CustomContextFactory : DefaultSpringGraphQLContextFactory() {
    override suspend fun generateContext(request: ServerRequest): GraphQLContext {
        return super.generateContext(request).plus(mapOf("custom" to (request.headers().firstHeader("X-Custom-Header") ?: UUID.randomUUID().toString())))
    }
}
