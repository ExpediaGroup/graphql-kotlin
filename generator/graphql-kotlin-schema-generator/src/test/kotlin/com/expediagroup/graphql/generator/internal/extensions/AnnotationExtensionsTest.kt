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

import com.expediagroup.graphql.generator.annotations.GraphQLDeprecated
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.annotations.GraphQLUnion
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
class AnnotationExtensionsTest {

    @GraphQLName("WithAnnotationsCustomName")
    @GraphQLDescription("class description")
    @Deprecated("class deprecated")
    @GraphQLIgnore
    private data class WithAnnotations(
        @property:Deprecated("property deprecated")
        @property:GraphQLDescription("property description")
        @property:GraphQLName("newName")
        val id: String,

        @GraphQLUnion(name = "CustomUnion", possibleTypes = [NoAnnotations::class])
        val union: Any,

        @property:MetaUnion
        val metaUnion: Any
    )

    private data class NoAnnotations(val id: String)

    @GraphQLUnion(name = "MetaUnion", possibleTypes = [NoAnnotations::class])
    annotation class MetaUnion

    @GraphQLDeprecated("class deprecated", ReplaceWith("WithAnnotations"))
    private data class WithGraphQLDeprecated(
        @property:GraphQLDeprecated("property deprecated", ReplaceWith("uuid"))
        val id: String,
        val uuid: String,
    )

    @Test
    fun `verify @GraphQLName on classes`() {
        @Suppress("DEPRECATION")
        assertEquals(expected = "WithAnnotationsCustomName", actual = WithAnnotations::class.getGraphQLName())
        assertNull(NoAnnotations::class.getGraphQLName())
    }

    @Test
    fun `verify @GraphQLName on fields`() {
        @Suppress("DEPRECATION")
        val fieldName = WithAnnotations::class.findMemberProperty("id")?.getGraphQLName()
        assertEquals(expected = "newName", actual = fieldName)
        assertNull(NoAnnotations::class.findMemberProperty("id")?.getGraphQLName())
    }

    @Test
    fun `verify @GraphQLDescrption on classes`() {
        @Suppress("DEPRECATION")
        assertEquals(expected = "class description", actual = WithAnnotations::class.getGraphQLDescription())
        assertNull(NoAnnotations::class.getGraphQLDescription())
    }

    @Test
    fun `verify @Deprecated`() {
        @Suppress("DEPRECATION")
        val classDeprecation = WithAnnotations::class.getDeprecationReason()
        @Suppress("DEPRECATION")
        val classPropertyDeprecation = WithAnnotations::class.findMemberProperty("id")?.getDeprecationReason()

        assertEquals(expected = "class deprecated", actual = classDeprecation)
        assertEquals(expected = "property deprecated", actual = classPropertyDeprecation)
        assertNull(NoAnnotations::class.getDeprecationReason())
        assertNull(NoAnnotations::class.findMemberProperty("id")?.getDeprecationReason())
    }

    @Test
    fun `verify @GraphQLDeprecated`() {
        val classDeprecation = WithGraphQLDeprecated::class.getDeprecationReason()
        val classPropertyDeprecation = WithGraphQLDeprecated::class.findMemberProperty("id")?.getDeprecationReason()

        assertEquals(expected = "class deprecated, replace with WithAnnotations", actual = classDeprecation)
        assertEquals(expected = "property deprecated, replace with uuid", actual = classPropertyDeprecation)
    }

    @Test
    fun `verify @GraphQLIgnore`() {
        @Suppress("DEPRECATION")
        assertTrue(WithAnnotations::class.isGraphQLIgnored())
        assertFalse(NoAnnotations::class.isGraphQLIgnored())
    }

    @Test
    fun `verify @GraphQLUnion`() {
        @Suppress("DEPRECATION")
        assertNotNull(WithAnnotations::class.findMemberProperty("union")?.annotations?.getUnionAnnotation())
        @Suppress("DEPRECATION")
        assertNull(WithAnnotations::class.findMemberProperty("union")?.annotations?.getCustomUnionClassWithMetaUnionAnnotation())
        @Suppress("DEPRECATION")
        assertNotNull(WithAnnotations::class.findMemberProperty("metaUnion")?.annotations?.getUnionAnnotation())
        @Suppress("DEPRECATION")
        assertNotNull(WithAnnotations::class.findMemberProperty("metaUnion")?.annotations?.getCustomUnionClassWithMetaUnionAnnotation())
        @Suppress("DEPRECATION")
        assertNull(WithAnnotations::class.findMemberProperty("id")?.annotations?.getUnionAnnotation())
        @Suppress("DEPRECATION")
        assertNull(WithAnnotations::class.findMemberProperty("id")?.annotations?.getCustomUnionClassWithMetaUnionAnnotation())
        @Suppress("DEPRECATION")
        assertNotNull(WithAnnotations::class.findMemberProperty("metaUnion")?.annotations?.firstOrNull { it is MetaUnion }?.getMetaUnionAnnotation())
        @Suppress("DEPRECATION")
        assertNull(WithAnnotations::class.findMemberProperty("union")?.annotations?.firstOrNull { it is GraphQLUnion }?.getMetaUnionAnnotation())
    }

    private fun KClass<*>.findMemberProperty(name: String) = this.declaredMemberProperties.find { it.name == name }
}
