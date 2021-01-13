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

package com.expediagroup.graphql.generator

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TopLevelObjectTest {

    internal class TestClass

    internal object TestObject

    @Test
    fun `basic container`() {
        val obj = TestClass()
        val top = TopLevelObject(obj)
        assertEquals(expected = obj, actual = top.obj)
        assertEquals(expected = TestClass::class, actual = top.kClass)
    }

    @Test
    fun `custom class`() {
        val top = TopLevelObject(TestObject, TestClass::class)
        assertEquals(expected = TestObject, actual = top.obj)
        assertEquals(expected = TestClass::class, actual = top.kClass)
    }
}
