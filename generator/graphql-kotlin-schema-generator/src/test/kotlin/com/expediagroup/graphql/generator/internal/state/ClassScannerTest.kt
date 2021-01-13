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

package com.expediagroup.graphql.generator.internal.state

import com.expediagroup.graphql.generator.defaultSupportedPackages
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClassScannerTest {

    interface NoSubTypesInterface

    abstract class NoSubTypesClass

    interface MyInterface {
        fun getValue(): Int
    }

    class FirstClass : MyInterface {
        override fun getValue() = 1
    }

    class SecondClass : MyInterface {
        override fun getValue() = 2
    }

    @Suppress("Detekt.UnnecessaryAbstractClass")
    abstract class MyAbstractClass {
        abstract fun someValue(): Int
    }

    class ThirdClass : MyAbstractClass() {
        override fun someValue() = 3
    }

    abstract class FourthClass : MyAbstractClass() {
        override fun someValue() = 3

        abstract fun getOtherValue(): Int
    }

    annotation class SimpleAnnotation
    annotation class OtherSimpleAnnotation

    @SimpleAnnotation
    class MyClassWithAnnotaiton

    private val basicClassScanner = ClassScanner(defaultSupportedPackages)

    @Test
    fun `valid subtypes`() {
        val list = basicClassScanner.getSubTypesOf(MyInterface::class)
        assertEquals(expected = 2, actual = list.size)
    }

    @Test
    fun `abstract subtypes`() {
        val list = basicClassScanner.getSubTypesOf(MyAbstractClass::class)
        assertEquals(expected = 1, actual = list.size)
    }

    @Test
    fun `subtypes of non-supported packages`() {
        val classScannerOfOtherPackages = ClassScanner(listOf("com.example"))
        classScannerOfOtherPackages.use {
            assertTrue(classScannerOfOtherPackages.isEmptyScan())
            val list = classScannerOfOtherPackages.getSubTypesOf(MyInterface::class)
            assertEquals(expected = 0, actual = list.size)
        }
    }

    @Test
    fun `interface with no subtypes`() {
        val list = basicClassScanner.getSubTypesOf(NoSubTypesInterface::class)
        assertEquals(expected = 0, actual = list.size)
    }

    @Test
    fun `abstract class with no subtypes`() {
        val list = basicClassScanner.getSubTypesOf(NoSubTypesClass::class)
        assertEquals(expected = 0, actual = list.size)
    }

    @Test
    fun `classes with annotation returns all values`() {
        val invalidClasses = basicClassScanner.getClassesWithAnnotation(OtherSimpleAnnotation::class)
        assertEquals(0, invalidClasses.size)

        val validClasses = basicClassScanner.getClassesWithAnnotation(SimpleAnnotation::class)
        assertEquals(1, validClasses.size)
    }

    companion object {
        @AfterAll
        fun afterAll(classScannerTest: ClassScannerTest) {
            classScannerTest.basicClassScanner.close()
        }
    }
}
