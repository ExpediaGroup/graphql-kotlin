/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.filters

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class CallableFiltersTest {

    @Test
    fun `test function filters`() {
        assertTrue(isValidFunction(MyClass::publicFunction))
        assertFalse(isValidFunction(MyClass::ignoredFunction))
        assertFalse(isValidFunction(MyClass::privateFunction))
        assertFalse(isValidFunction(MyClass::`invalid$Name`))
    }

    @Test
    fun `test generated function filters`() {
        val functions = MyDataClass::class.declaredMemberFunctions
        val size = functions.filter { func -> functionFilters.all { it(func) } }.size
        assertEquals(expected = 0, actual = size)
    }

    internal data class MyDataClass(val id: Int = 0)

    internal class MyClass {
        fun publicFunction() = privateFunction()

        @GraphQLIgnore
        fun ignoredFunction() = privateFunction()

        @Suppress("Detekt.FunctionOnlyReturningConstant")
        internal fun privateFunction() = 0

        fun `invalid$Name`() = privateFunction()
    }

    private fun isValidFunction(function: KFunction<*>): Boolean = functionFilters.all { it(function) }
}
