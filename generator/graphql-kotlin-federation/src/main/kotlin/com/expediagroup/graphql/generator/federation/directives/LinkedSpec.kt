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
 * Meta annotation used to indicate that given directive/type is associated with imported `@link` specification.
 *
 * Example usage:
 * ```
 * @LinkedSpec(FEDERATION_SPEC)
 * @Repeatable
 * @GraphQLDirective(
 *     name = KEY_DIRECTIVE_NAME,
 *     description = KEY_DIRECTIVE_DESCRIPTION,
 *     locations = [DirectiveLocation.OBJECT, DirectiveLocation.INTERFACE]
 * )
 * annotation class KeyDirective(val fields: FieldSet, val resolvable: Boolean = true)
 * ```
 */
@Target(allowedTargets = [AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS])
annotation class LinkedSpec(val value: String)
