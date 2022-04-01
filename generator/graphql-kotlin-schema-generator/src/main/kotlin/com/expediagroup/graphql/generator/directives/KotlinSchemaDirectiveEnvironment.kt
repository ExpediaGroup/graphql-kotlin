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

package com.expediagroup.graphql.generator.directives

import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirectiveContainer

/**
 * KotlinSchemaDirectiveEnvironment holds basic wiring information that includes target element and directive that is to be applied.
 */
open class KotlinSchemaDirectiveEnvironment<out T : GraphQLDirectiveContainer>(
    val element: T,
    val directive: GraphQLAppliedDirective,
    val codeRegistry: GraphQLCodeRegistry.Builder
)
