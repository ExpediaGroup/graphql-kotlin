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

package com.expediagroup.graphql.client.serialization.data

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.data.scalars.JsonObjectSerializer
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.serialization.data.entitiesquery._Entity
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
class EntitiesQuery(
    override val variables: Variables,
) : GraphQLClientRequest<EntitiesQuery.Result> {
    @Required
    override val query: String = "ENTITIES_QUERY"

    @Required
    override val operationName: String = "EntitiesQuery"

    override fun responseType(): KClass<Result> = Result::class

    @Generated
    @Serializable
    public data class Variables(
        public val representations: List<@Serializable(with = JsonObjectSerializer::class) JsonObject>,
    )

    @Generated
    @Serializable
    public data class Result(
        /**
         * Union of all types that use the @key directive, including both types native to the schema and
         * extended types
         */
        public val _entities: List<_Entity?>,
    )
}
