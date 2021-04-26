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

package com.expediagroup.graphql.generator.annotations

/**
 * Kotlin classes can be used as GraphQL input types, output types, or both.
 * If you want to enforce that a Kotlin class only be used as an input or output type,
 * include this annotation with the preferred location parameter.
 *
 * By default, classes will be allowed as both input and output types.
 */
annotation class GraphQLValidObjectLocations(val locations: Array<Locations>) {
    enum class Locations {
        OBJECT,
        INPUT_OBJECT
    }
}
