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

package com.expediagroup.graphql.generator.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
/**
 * Specifies GraphQL `@oneOf` field name for a sealed input subtype.
 *
 * Used when a sealed type marked with [GraphQLOneOf] is generated as a GraphQL
 * `@oneOf` input object. The value becomes field name that maps to this subtype
 * during schema generation and runtime input conversion.
 */
annotation class GraphQLOneOfField(
    val fieldName: String,
    val type: GraphQLOneOfFieldType = GraphQLOneOfFieldType.WRAPPED
)

/**
 * Defines how a sealed subtype annotated with [GraphQLOneOfField] is represented
 * as a field of the generated GraphQL `@oneOf` inputObject.
 */
enum class GraphQLOneOfFieldType {
    /**
     * Generate the oneOf field as an input object using the annotated subtype.
     *
     * Example:
     * ```
     * @GraphQLOneOf
     * sealed interface UserByInput
     *
     * @GraphQLOneOfField(name = "criteria", type = GraphQLOneOfFieldType.WRAPPED)
     * data class Criteria(val name: String, val address: String) : UserByInput
     * ```
     *
     * Generates:
     * ```graphql
     * input UserByInput @oneOf {
     *   criteria: Criteria
     * }
     * ```
     */
    WRAPPED,
    /**
     * Generate the oneOf field using the annotated subtype's single constructor
     * property type directly.
     *
     * The annotated subtype must define exactly one primary constructor property.
     *
     * Example:
     * ```
     * @GraphQLOneOf
     * sealed interface UserByInput
     *
     * @GraphQLOneOfField(name = "email", type = GraphQLOneOfFieldType.UNWRAPPED)
     * data class UserByEmail(val email: String) : UserByInput
     * ```
     *
     * Generates:
     *
     * ```graphql
     * input UserByInput @oneOf {
     *   email: String
     * }
     * ```
     *
     * If [WRAPPED] is used in this use case the generated SDL would've been:
     *
     * ```graphql
     * input UserByInput @oneOf {
     *   email: UserByEmailInput
     * }
     *
     * input UserByEmailInput {
     *  email: String
     * }
     * ```
     *
     */
    UNWRAPPED
}
