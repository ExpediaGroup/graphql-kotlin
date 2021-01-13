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

package com.expediagroup.graphql.generator.internal.extensions

import com.expediagroup.graphql.generator.annotations.GraphQLName
import org.junit.jupiter.api.Test
import kotlin.reflect.full.functions
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KCallableExtensionsKtTest {

    @Suppress("Detekt.FunctionOnlyReturningConstant", "Detekt.UnusedPrivateMember")
    open class MyTestClass {
        fun public() = 1

        protected fun protected() = 2

        internal fun internal() = 3

        private fun private() = 4

        @GraphQLName("renamedFunction")
        fun originalName() = 1
    }

    @Test
    fun isPublic() {
        assertTrue(MyTestClass::public.isPublic())
        assertFalse(MyTestClass::internal.isPublic())
        assertFalse(MyTestClass::class.functions.find { it.name == "protected" }?.isPublic().isTrue())
        assertFalse(MyTestClass::class.functions.find { it.name == "private" }?.isPublic().isTrue())
    }

    @Test
    fun graphQLName() {
        assertEquals(expected = "renamedFunction", actual = MyTestClass::originalName.getFunctionName())
        assertEquals(expected = "public", actual = MyTestClass::public.getFunctionName())
    }
}
