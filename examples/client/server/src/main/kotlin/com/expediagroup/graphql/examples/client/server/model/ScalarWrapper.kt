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

package com.expediagroup.graphql.examples.client.server.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import com.ibm.icu.util.ULocale
import java.util.UUID

@GraphQLDescription("Wrapper that holds all supported scalar types")
data class ScalarWrapper(
    @GraphQLDescription("ID represents unique identifier that is not intended to be human readable")
    val id: ID,
    @GraphQLDescription("UTF-8 character sequence")
    val name: String,
    @GraphQLDescription("Either true or false")
    val valid: Boolean,
    @GraphQLDescription("A signed 32-bit nullable integer value")
    val count: Int?,
    @GraphQLDescription("A nullable signed double-precision floating-point value")
    val rating: Float?,
    @GraphQLDescription("Custom scalar of UUID")
    val custom: UUID,
    @GraphQLDescription("List of custom scalar UUIDs")
    val customList: List<UUID>,
    @GraphQLDescription("Custom scalar of Locale")
    val locale: ULocale,
    @GraphQLDescription("List of custom scalar Locales")
    val listLocale: List<ULocale>
)
