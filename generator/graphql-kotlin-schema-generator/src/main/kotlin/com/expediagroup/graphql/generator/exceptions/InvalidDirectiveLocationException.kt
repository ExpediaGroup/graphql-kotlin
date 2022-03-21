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

package com.expediagroup.graphql.generator.exceptions

import graphql.introspection.Introspection

/**
 * Thrown when directive is specified on unsupported location.
 */
class InvalidDirectiveLocationException(
    directiveName: String,
    supportedLocations: Array<Introspection.DirectiveLocation>,
    location: Introspection.DirectiveLocation,
    target: String
) : GraphQLKotlinException(
    "Directive $directiveName was specified in unsupported $location location ($target). This directive can only be applied on one of the supported locations: ${supportedLocations.joinToString()}."
)
