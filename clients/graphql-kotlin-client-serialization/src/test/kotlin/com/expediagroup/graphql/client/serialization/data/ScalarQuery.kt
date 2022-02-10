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

import com.expediagroup.graphql.client.serialization.data.scalars.UUIDSerializer
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.reflect.KClass

// typealiases would be in separate file
typealias ID = String

@Serializable
class ScalarQuery(
    override val variables: Variables
) : GraphQLClientRequest<ScalarQuery.Result> {
    @Required
    override val query: String = "SCALAR_QUERY"

    @Required
    override val operationName: String = "ScalarQuery"

    override fun responseType(): KClass<Result> = Result::class

    @Serializable
    data class Variables(
        val alias: ID? = null,
        @Serializable(with = UUIDSerializer::class)
        val custom: UUID? = null
    )

    @Serializable
    data class Result(
        val scalarAlias: ID,
        @Serializable(with = UUIDSerializer::class)
        val customScalar: UUID,
        val customScalarList: List<@Serializable(with = UUIDSerializer::class) UUID>
    )
}
