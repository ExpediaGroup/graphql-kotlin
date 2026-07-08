/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.directives

/**
 * Annotation representing the `ContextFieldValue` scalar type that is used by the `@fromContext` directive.
 *
 * A context field value is a selection statement that resolves a value from a `@context` set on an ancestor in the
 * query, e.g. `"$myContext { field }"`.
 *
 * @param value the selection statement used to resolve a value from a matching `@context`
 *
 * @see [com.expediagroup.graphql.generator.federation.types.CONTEXT_FIELD_VALUE_SCALAR_TYPE]
 * @see FromContextDirective
 */
@LinkedSpec(FEDERATION_SPEC)
annotation class ContextFieldValue(val value: String)
