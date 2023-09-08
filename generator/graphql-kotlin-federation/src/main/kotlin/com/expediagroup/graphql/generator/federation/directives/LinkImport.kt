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

package com.expediagroup.graphql.generator.federation.directives

/**
 * Annotation representing Import scalar type that is used to by @link directive to import types from a specification.
 *
 * When importing schema elements we can either:
 * - import elements directly, i.e. use name that matches the type name in the imported specification, e.g. `@key`
 * - specify custom name for imported elements (allows to avoid schema collisions), e.g. `{ name = "@key`, as = "@myKey"}`
 *
 * @param name original imported type name
 * @param `as` imported type name in the schema
 *
 * @see [com.expediagroup.graphql.generator.federation.types.LINK_IMPORT_SCALAR_TYPE]
 */
@LinkedSpec(LINK_SPEC)
annotation class LinkImport(val name: String, val `as`: String = "")
