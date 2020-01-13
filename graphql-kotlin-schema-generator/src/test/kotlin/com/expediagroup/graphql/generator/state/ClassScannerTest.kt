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

package com.expediagroup.graphql.generator.state

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Suppress("Detekt.UnusedPrivateClass")
internal class ClassScannerTest {

    private interface NoSubTypesInterface

    private abstract class NoSubTypesClass

    private interface MyInterface {
        fun getValue(): Int
    }

    private class FirstClass : MyInterface {
        override fun getValue() = 1
    }

    private class SecondClass : MyInterface {
        override fun getValue() = 2
    }

    @Suppress("Detekt.UnnecessaryAbstractClass")
    private abstract class MyAbstractClass {
        abstract fun someValue(): Int
    }

    private class ThirdClass : MyAbstractClass() {
        override fun someValue() = 3
    }

    private abstract class FourthClass : MyAbstractClass() {
        override fun someValue() = 3

        abstract fun getOtherValue(): Int
    }

    private annotation class SimpleAnnotation
    private annotation class OtherSimpleAnnotation

    @SimpleAnnotation
    private class MyClassWithAnnotaiton

    @Test
    fun `valid subtypes`() {
        val classScanner = ClassScanner(listOf("com.expediagroup.graphql"))
        val list = classScanner.getSubTypesOf(MyInterface::class)

        assertEquals(expected = 2, actual = list.size)
    }

    @Test
    fun `abstract subtypes`() {
        val classScanner = ClassScanner(listOf("com.expediagroup.graphql"))
        val list = classScanner.getSubTypesOf(MyAbstractClass::class)

        assertEquals(expected = 1, actual = list.size)
    }

    @Test
    fun `subtypes of non-supported packages`() {
        val classScanner = ClassScanner(listOf("com.example"))
        val list = classScanner.getSubTypesOf(MyInterface::class)

        assertEquals(expected = 0, actual = list.size)
    }

    @Test
    fun `interface with no subtypes`() {
        val classScanner = ClassScanner(listOf("com.expediagroup.graphql"))
        val list = classScanner.getSubTypesOf(NoSubTypesInterface::class)

        assertEquals(expected = 0, actual = list.size)
    }

    @Test
    fun `abstract class with no subtypes`() {
        val classScanner = ClassScanner(listOf("com.expediagroup.graphql"))
        val list = classScanner.getSubTypesOf(NoSubTypesClass::class)

        assertEquals(expected = 0, actual = list.size)
    }

    @Test
    fun `classes with annotation returns all values`() {
        val classScanner = ClassScanner(listOf("com.expediagroup.graphql.generator"))

        val invalidClasses = classScanner.getClassesWithAnnotation(OtherSimpleAnnotation::class)
        assertEquals(0, invalidClasses.size)

        val validClasses = classScanner.getClassesWithAnnotation(SimpleAnnotation::class)
        assertEquals(1, validClasses.size)
    }
}
