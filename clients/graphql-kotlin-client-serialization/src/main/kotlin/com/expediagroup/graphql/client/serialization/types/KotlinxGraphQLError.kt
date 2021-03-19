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

package com.expediagroup.graphql.client.serialization.types

import com.expediagroup.graphql.client.serialization.serializers.AnyKSerializer
import com.expediagroup.graphql.client.serialization.serializers.GraphQLErrorPathSerializer
import com.expediagroup.graphql.client.types.GraphQLClientError
import kotlinx.serialization.Serializable

@Serializable
data class KotlinxGraphQLError(
    override val message: String,
    override val locations: List<KotlinxGraphQLSourceLocation>? = null,
    override val path: List<@Serializable(with = GraphQLErrorPathSerializer::class) Any>? = null,
    override val extensions: Map<String, @Serializable(with = AnyKSerializer::class) Any?>? = null
) : GraphQLClientError
