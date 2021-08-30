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

package com.expediagroup.graphql.client.serialization.data

import com.expediagroup.graphql.client.serialization.data.enums.TestEnum
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
class EnumQuery(
    override val variables: Variables
) : GraphQLClientRequest<EnumQuery.Result> {
    @Required
    override val query: String = "ENUM_QUERY"

    @Required
    override val operationName: String = "EnumQuery"

    override fun responseType(): KClass<Result> = Result::class

    @Serializable
    data class Variables(
        val enum: TestEnum? = null
    )

    @Serializable
    data class Result(
        val enumResult: TestEnum = TestEnum.__UNKNOWN
    )
}
