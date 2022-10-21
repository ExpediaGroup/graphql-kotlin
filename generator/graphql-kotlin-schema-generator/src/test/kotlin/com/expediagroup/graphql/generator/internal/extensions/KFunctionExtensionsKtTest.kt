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

package com.expediagroup.graphql.generator.internal.extensions

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import graphql.schema.DataFetchingEnvironment
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class KFunctionExtensionsKtTest {

    @Test
    fun getValidArguments() {
        val args = TestingClass::happyPath.getValidArguments()
        assertEquals(expected = 1, actual = args.size)
        assertEquals(expected = "color", actual = args.first().getName())
    }

    @Test
    fun `getValidArguments should ignore @GraphQLIgnore`() {
        val args = TestingClass::ignored.getValidArguments()
        assertEquals(expected = 1, actual = args.size)
        assertEquals(expected = "notIgnored", actual = args.first().getName())
    }

    @Test
    fun `getValidArguments should ignore DataFetchingEnvironment`() {
        val args = TestingClass::dataFetchingEnvironment.getValidArguments()
        assertEquals(expected = 1, actual = args.size)
        assertEquals(expected = "notEnvironment", actual = args.first().getName())
    }

    private class TestingClass {
        fun happyPath(color: String) = "You're color is $color"

        fun ignored(@GraphQLIgnore ignoredArg: String, notIgnored: String) = "$ignoredArg and $notIgnored"

        fun dataFetchingEnvironment(environment: DataFetchingEnvironment, notEnvironment: String): String = "${environment.field.name} and $notEnvironment"
    }
}
