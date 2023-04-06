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

package com.expediagroup.graphql.plugin.graalvm.list

import com.expediagroup.graphql.server.operations.Query

class ListQuery : Query {
    fun listQuery(): List<Int>? = TODO()
    fun listObjectQuery(): List<OutputOnly> = TODO()
    fun listPrimitiveArg(arg: List<Int>): List<Int> = TODO()
    fun listObjectArg(arg: List<InputOnly>): String = TODO()
    fun optionalListArg(arg: List<String>? = null): List<String>? = TODO()
}

data class InputOnly(val id: Int)
data class OutputOnly(val id: Int, val description: String) {
    fun calculate(): Int = TODO()
}
