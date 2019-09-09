/*
 * Copyright 2019 Expedia Group, Inc.
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
internal class UnionBuilderTest : TypeTestHelper() {

    private lateinit var builder: UnionBuilder

    override fun beforeTest() {
        builder = UnionBuilder(generator)
    }

    @GraphQLDescription("The truth")
    @SimpleDirective
    private interface Cake

    @GraphQLDescription("so red")
    private class StrawBerryCake : Cake

    @GraphQLName("CakeRenamed")
    private interface CakeCustomName

    @GraphQLName("StrawBerryCakeRenamed")
    private class StrawBerryCakeCustomName : CakeCustomName

    private interface NestedUnionA

    private interface NestedUnionB

    private class NestedClass : NestedUnionA, NestedUnionB {
        fun getUnionA(): NestedUnionA = NestedClass()
        fun getUnionB(): NestedUnionB = NestedClass()
    }

    @Test
    fun `Test naming`() {
        val result = builder.unionType(Cake::class) as? GraphQLUnionType
        assertNotNull(result)

        assertEquals(Cake::class.java.simpleName, result.name)
        assertEquals(1, result.types.size)
        assertEquals(StrawBerryCake::class.java.simpleName, result.types[0].name)
    }

    @Test
    fun `Test custom naming`() {
        val result = builder.unionType(CakeCustomName::class) as? GraphQLUnionType
        assertNotNull(result)

        assertEquals("CakeRenamed", result.name)
        assertEquals(1, result.types.size)
        assertEquals("StrawBerryCakeRenamed", result.types[0].name)
    }

    @Test
    fun `Test description`() {
        val result = builder.unionType(Cake::class) as? GraphQLUnionType
        assertNotNull(result)

        assertEquals("The truth", result.description)
        assertEquals(1, result.types.size)
        assertEquals("so red", (result.types[0] as? GraphQLObjectType)?.description)
    }

    @Test
    fun `Unions can have directives`() {
        val result = builder.unionType(Cake::class) as? GraphQLUnionType

        assertNotNull(result)
        assertEquals(1, result.directives.size)
        assertEquals("simpleDirective", result.directives.first().name)
    }

    @Test
    fun `verify union is build only once`() {
        val cache = generator.state.cache
        assertTrue(cache.doesNotContain(Cake::class))

        val first = builder.unionType(Cake::class) as? GraphQLUnionType
        assertNotNull(first)
        assertFalse(cache.doesNotContain(Cake::class))
        val second = builder.unionType(Cake::class) as? GraphQLUnionType
        assertNotNull(second)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun `verify nested classes resovle the type reference in the gererator`() {
        val cache = generator.state.cache
        assertTrue(cache.doesNotContain(NestedUnionA::class))

        val unionType = builder.unionType(NestedUnionA::class) as? GraphQLUnionType
        assertNotNull(unionType)
        assertFalse(cache.doesNotContain(NestedUnionA::class))
    }
}
