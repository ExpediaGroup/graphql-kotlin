/*
 * Copyright 2019 Expedia Group
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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLID
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class AnnotationExtensionsTest {

    @GraphQLName("WithAnnotationsCustomName")
    @GraphQLDescription("class description")
    @Deprecated("class deprecated")
    @GraphQLIgnore
    private data class WithAnnotations(
        @property:Deprecated("property deprecated")
        @property:GraphQLDescription("property description")
        @property:GraphQLID
        @property:GraphQLName("newName")
        val id: String
    )

    private data class NoAnnotations(val id: String)

    @Test
    fun `verify @GraphQLName on classes`() {
        assertEquals(expected = "WithAnnotationsCustomName", actual = WithAnnotations::class.getGraphQLName())
        assertNull(NoAnnotations::class.getGraphQLName())
    }

    @Test
    fun `verify @GraphQLName on fields`() {
        val fieldName = WithAnnotations::class.findMemberProperty("id")?.getGraphQLName()
        assertEquals(expected = "newName", actual = fieldName)
        assertNull(NoAnnotations::class.findMemberProperty("id")?.getGraphQLName())
    }

    @Test
    fun `verify @GraphQLDescrption on classes`() {
        assertEquals(expected = "class description", actual = WithAnnotations::class.getGraphQLDescription())
        assertNull(NoAnnotations::class.getGraphQLDescription())
    }

    @Test
    fun `verify @Deprecated`() {
        val classDeprecation = WithAnnotations::class.getDeprecationReason()
        val classPropertyDeprecation = WithAnnotations::class.findMemberProperty("id")?.getDeprecationReason()

        assertEquals(expected = "class deprecated", actual = classDeprecation)
        assertEquals(expected = "property deprecated", actual = classPropertyDeprecation)
        assertNull(NoAnnotations::class.getDeprecationReason())
        assertNull(NoAnnotations::class.findMemberProperty("id")?.getDeprecationReason())
    }

    @Test
    fun `verify @GraphQLIgnore`() {
        assertTrue(WithAnnotations::class.isGraphQLIgnored())
        assertFalse(NoAnnotations::class.isGraphQLIgnored())
    }

    @Test
    fun `verify @GraphQLID`() {
        val id = WithAnnotations::class.findMemberProperty("id")
        val notId = NoAnnotations::class.findMemberProperty("id")
        assertTrue { id?.isGraphQLID().isTrue() }
        assertFalse { notId?.isGraphQLID().isTrue() }
    }

    private fun KClass<*>.findMemberProperty(name: String) = this.declaredMemberProperties.find { it.name == name }
}
