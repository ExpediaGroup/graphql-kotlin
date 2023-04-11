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

package com.expediagroup.graalvm.ktor.schema

import com.expediagroup.graalvm.ktor.schema.model.InputAndOutput
import com.expediagroup.graalvm.ktor.schema.model.InputOnly
import com.expediagroup.graalvm.ktor.schema.model.OutputOnly
import com.expediagroup.graphql.server.operations.Query

/**
 * Queries verifying handling of input/output types.
 */
class TypesQuery : Query {
    fun outputTypeQuery(): OutputOnly = OutputOnly(id = 123, description = "foobar")
    fun inputTypeQuery(arg: InputOnly? = null): String? = arg?.toString()
    fun inputAndOutputQuery(arg: InputAndOutput? = null): InputAndOutput? = arg
}
