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

package com.expediagroup.graphql.generator.federation.execution

import com.apollographql.federation.graphqljava.tracing.HTTPRequestHeaders
import com.expediagroup.graphql.generator.execution.GraphQLContext

/**
 * Apollo federation needs access to the HTTP headers to inspect if the
 * request came from the Apollo Gateway. That means we need a special interface
 * for the federation context.
 */
@Deprecated(message = "The generic context object is deprecated in favor of the context map", ReplaceWith("graphql.GraphQLContext"))
interface FederatedGraphQLContext : GraphQLContext, HTTPRequestHeaders
