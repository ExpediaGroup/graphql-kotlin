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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.test.utils.SimpleDirective
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class GenerateUnionTest : TypeTestHelper() {

    @GraphQLDescription("The truth")
    @SimpleDirective
    private interface Cake

    @Suppress("Detekt.FunctionOnlyReturningConstant")
    @GraphQLDescription("so red")
    private class StrawBerryCake : Cake {
        fun recipe(): String = "google it"
    }

    @GraphQLName("CakeRenamed")
    private interface CakeCustomName

    @Suppress("Detekt.FunctionOnlyReturningConstant")
    @GraphQLName("StrawBerryCakeRenamed")
    private class StrawBerryCakeCustomName : CakeCustomName {
        fun recipe(): String = "bing it"
    }

    private interface NestedUnionA

    private interface NestedUnionB

    private class NestedClass : NestedUnionA, NestedUnionB {
        fun getUnionA(): NestedUnionA = NestedClass()
        fun getUnionB(): NestedUnionB = NestedClass()
    }

    @Test
    fun `Test simple case`() {
        val result = generateUnion(generator, Cake::class) as? GraphQLUnionType
        assertNotNull(result)

        assertEquals("Cake", result.name)
        assertEquals(1, result.types.size)
        assertEquals("StrawBerryCake", result.types[0].name)
    }

    @Test
    fun `Test custom naming`() {
        val result = generateUnion(generator, CakeCustomName::class) as? GraphQLUnionType
        assertNotNull(result)

        assertEquals("CakeRenamed", result.name)
        assertEquals(1, result.types.size)
        assertEquals("StrawBerryCakeRenamed", result.types[0].name)
    }

    @Test
    fun `Test description`() {
        val result = generateUnion(generator, Cake::class) as? GraphQLUnionType
        assertNotNull(result)

        assertEquals("The truth", result.description)
        assertEquals(1, result.types.size)
        assertEquals("so red", (result.types[0] as? GraphQLObjectType)?.description)
    }

    @Test
    fun `Unions can have directives`() {
        val result = generateUnion(generator, Cake::class) as? GraphQLUnionType

        assertNotNull(result)
        assertEquals(1, result.directives.size)
        assertEquals("simpleDirective", result.directives.first().name)
    }

    @Test
    fun `verify nested classes resovle the type reference in the gererator`() {
        val cache = generator.cache
        assertTrue(cache.doesNotContain(NestedUnionA::class))

        val unionType = generateUnion(generator, NestedUnionA::class) as? GraphQLUnionType
        assertNotNull(unionType)
        assertFalse(cache.doesNotContain(NestedUnionA::class))
    }
}
