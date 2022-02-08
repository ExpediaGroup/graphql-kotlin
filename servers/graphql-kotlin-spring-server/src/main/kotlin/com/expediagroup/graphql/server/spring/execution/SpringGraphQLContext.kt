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

package com.expediagroup.graphql.server.spring.execution

import com.expediagroup.graphql.generator.federation.execution.FederatedGraphQLContext
import org.springframework.web.reactive.function.server.ServerRequest

/**
 * Implements the [FederatedGraphQLContext] to provide support for federation tracing.
 * The class can be extended if other custom fields are needed.
 */
@Deprecated(message = "The generic context object is deprecated in favor of the context map", ReplaceWith("graphql.GraphQLContext"))
open class SpringGraphQLContext(private val request: ServerRequest) : FederatedGraphQLContext {
    override fun getHTTPRequestHeader(caseInsensitiveHeaderName: String): String? =
        request.headers().firstHeader(caseInsensitiveHeaderName)
}
