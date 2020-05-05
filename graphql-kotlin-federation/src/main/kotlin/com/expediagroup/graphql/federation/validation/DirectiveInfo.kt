/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.federation.validation

/**
 * Internal class to represent all the extracted info
 * about a directive that we are validating for federation.
 */
internal data class DirectiveInfo(
    val directiveName: String,
    val fieldSet: String,
    val typeName: String
)

/**
 * Extension method to get the GraphQL schema format
 * of the directive info that we print to errors.
 */
internal fun DirectiveInfo.getErrorString() = "@$directiveName(fields = $fieldSet) directive on $typeName"
