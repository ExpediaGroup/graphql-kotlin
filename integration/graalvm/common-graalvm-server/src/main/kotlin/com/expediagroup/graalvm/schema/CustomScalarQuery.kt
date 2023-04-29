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

package com.expediagroup.graalvm.schema

import com.expediagroup.graphql.server.operations.Query
import java.util.UUID

/**
 * Queries used to verify custom scalar functionality.
 */
class CustomScalarQuery : Query {
    fun customScalarQuery(): UUID = UUID.randomUUID()
    fun nullableCustomScalarQuery(): UUID? = null
    fun customScalarArg(arg: UUID): UUID = arg
    fun optionalCustomScalarArg(arg: UUID? = null): UUID? = arg
}
