/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.server.spring.extensions

import org.junit.jupiter.api.Test
import org.springframework.aop.framework.ProxyFactory
import org.springframework.stereotype.Component
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeneratorExtensionsKtTest {

    @Component
    class MyClass(val id: Int)

    @Test
    fun `toTopLevelObjects with basic class`() {
        val myObject = MyClass(1)
        val result = listOf(myObject).toTopLevelObjects()
        assertEquals(MyClass::class, result.first().kClass)
        assertEquals(myObject, result.first().obj)
    }

    @Test
    fun `toTopLevelObjects with AOP class`() {
        val myObject = MyClass(2)
        val proxyFactory = ProxyFactory(myObject)
        val result = listOf(proxyFactory.proxy).toTopLevelObjects()
        assertEquals(MyClass::class, result.first().kClass)
        assertTrue(result.first().obj is MyClass)
    }
}
