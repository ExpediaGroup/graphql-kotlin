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

package com.expediagroup.graphql.generator.internal.types

/**
 * Internal metadata class we can use to forward info about the type we are generating.
 * If there is no metadata to add, create the class with default values.
 */
internal data class GraphQLKTypeMetadata(
    val inputType: Boolean = false,
    val fieldName: String? = null,
    val fieldAnnotations: List<Annotation> = emptyList(),
    val isDirective: Boolean = false
)
