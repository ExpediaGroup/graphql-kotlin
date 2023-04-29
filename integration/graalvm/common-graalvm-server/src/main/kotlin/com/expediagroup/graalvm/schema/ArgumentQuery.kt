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

import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query

/**
 * Queries verifying passing scalar arguments.
 */
class ArgumentQuery : Query {

    fun stringArg(arg: String): String = arg.reversed()
    fun optionalStringArg(arg: String? = null): String? = arg?.reversed()
    fun intArg(arg: Int): Int = arg.inc()
    fun optionalIntArg(arg: Int? = null): Int? = arg?.inc()
    fun doubleArg(arg: Double): Double = arg.inc()
    fun optionalDoubleArg(arg: Double? = null): Double? = arg?.inc()
    fun booleanArg(arg: Boolean): Boolean = arg.not()
    fun optionalBooleanArg(arg: Boolean? = null): Boolean? = arg?.not()
    fun manyOptionalArgs(
        idArg: ID? = null,
        stringArg: String? = null,
        intArg: Int? = null,
        doubleArg: Double? = null,
        booleanArg: Boolean? = null
    ): String? = if (idArg != null || stringArg != null || intArg != null || doubleArg != null || booleanArg != null) {
        "${idArg?.value}|$stringArg|$intArg|$doubleArg|$booleanArg"
    } else {
        null
    }
}
