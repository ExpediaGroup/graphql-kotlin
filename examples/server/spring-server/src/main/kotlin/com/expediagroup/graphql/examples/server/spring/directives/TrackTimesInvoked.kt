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

package com.expediagroup.graphql.examples.server.spring.directives

import com.expediagroup.graphql.generator.annotations.GraphQLDirective

/**
 * Used to verify the performance overhead of instrumentation on fields.
 * Marker directive only, does not have DirectiveWiring.
 */
@GraphQLDirective(
    name = TRACK_TIMES_INVOKED_DIRECTIVE_NAME,
    description = "If the field is marked with this directive, " +
        "we keep track of how many times this field was invoked per exection " +
        "and log the result server side through graphql-java Instrumentation"
)
annotation class TrackTimesInvoked

const val TRACK_TIMES_INVOKED_DIRECTIVE_NAME = "trackTimesInvoked"
