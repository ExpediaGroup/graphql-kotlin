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

package com.expediagroup.graphql.client.jackson.data

import com.expediagroup.graphql.client.jackson.data.entitiesquery._Entity
import com.expediagroup.graphql.client.jackson.data.scalars.AnyToAnyConverter
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlin.reflect.KClass

class EntitiesQuery(
    override val variables: Variables,
) : GraphQLClientRequest<EntitiesQuery.Result> {
    override val query: String = "ENTITIES_QUERY"

    override val operationName: String = "EntitiesQuery"

    override fun responseType(): KClass<Result> = Result::class

    data class Variables(
        @JsonSerialize(contentConverter = AnyToAnyConverter::class)
        @JsonDeserialize(contentConverter = AnyToAnyConverter::class)
        @get:JsonProperty("representations")
        public val representations: List<Any>,
    )

    data class Result(
        /**
         * Union of all types that use the @key directive, including both types native to the schema and
         * extended types
         */
        val _entities: List<_Entity?>,
    )
}
