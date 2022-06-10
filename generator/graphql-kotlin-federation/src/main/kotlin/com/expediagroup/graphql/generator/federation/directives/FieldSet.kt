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

package com.expediagroup.graphql.generator.federation.directives

/**
 * Annotation representing _FieldSet scalar type that is used to represent a set of fields.
 *
 * Field set can represent:
 * - single field, e.g. "id"
 * - multiple fields, e.g. "id name"
 * - nested selection sets, e.g. "id user { name }"
 *
 * @param value field set that represents a set of fields forming the key
 *
 * @see [com.expediagroup.graphql.generator.federation.types.FIELD_SET_SCALAR_TYPE]
 */
annotation class FieldSet(val value: String)
