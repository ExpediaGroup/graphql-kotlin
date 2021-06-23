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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.annotations.GraphQLUnion
import com.expediagroup.graphql.generator.test.utils.SimpleDirective
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Suppress("Detekt.UnusedPrivateClass", "Detekt.FunctionOnlyReturningConstant")
class GenerateUnionTest : TypeTestHelper() {

    @GraphQLDescription("The truth")
    @SimpleDirective
    interface Cake

    @GraphQLDescription("so red")
    class StrawBerryCake : Cake {
        fun recipe(): String = "google it"
    }

    @GraphQLName("CakeRenamed")
    interface CakeCustomName

    @GraphQLName("StrawBerryCakeRenamed")
    class StrawBerryCakeCustomName : CakeCustomName {
        fun recipe(): String = "bing it"
    }

    interface NestedUnionA

    interface NestedUnionB

    class NestedClass : NestedUnionA, NestedUnionB {
        fun getUnionA(): NestedUnionA = NestedClass()
        fun getUnionB(): NestedUnionB = NestedClass()
    }

    class AnnotationUnion {
        @GraphQLUnion(name = "Foo", possibleTypes = [StrawBerryCakeCustomName::class, StrawBerryCake::class])
        fun foo(withName: Boolean): Any = if (withName) StrawBerryCakeCustomName() else StrawBerryCake()
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
    fun `custom union can be returned`() {
        val annotation = AnnotationUnion::foo.annotations.first() as GraphQLUnion
        val result = generateUnion(generator, Any::class, annotation)

        assertEquals("Foo", result.name)
        assertEquals(2, result.types.size)
        assertEquals("StrawBerryCakeRenamed", result.types[0].name)
        assertEquals("StrawBerryCake", result.types[1].name)
    }
}
