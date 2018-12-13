package com.expedia.graphql.schema.extensions

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.annotations.GraphQLIgnore
import kotlin.reflect.full.declaredMemberProperties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class AnnotationExtensionsTest {

    @GraphQLDescription("class description")
    @Deprecated("class deprecated")
    @GraphQLIgnore
    private data class WithAnnotations(
        @property:Deprecated("property deprecated")
        @property:GraphQLDescription("property description")
        @property:GraphQLID
        val id: String
    )

    private data class NoAnnotations(val id: String)

    @Test
    fun `verify @GraphQLDescrption on classes`() {
        assertEquals(expected = "class description", actual = WithAnnotations::class.getGraphQLDescription())
        assertNull(NoAnnotations::class.getGraphQLDescription())
    }

    @Test
    fun `verify @Deprecated`() {
        val classDeprecation = WithAnnotations::class.getDeprecationReason()
        val classPropertyDeprecation = WithAnnotations::class.declaredMemberProperties.find { it.name == "id" }?.getDeprecationReason()

        assertEquals(expected = "class deprecated", actual = classDeprecation)
        assertEquals(expected = "property deprecated", actual = classPropertyDeprecation)
        assertNull(NoAnnotations::class.getDeprecationReason())
    }

    @Test
    fun `verify @GraphQLIgnore`() {
        assertTrue(WithAnnotations::class.isGraphQLIgnored())
        assertFalse(NoAnnotations::class.isGraphQLIgnored())
    }

    @Test
    fun `verify @GraphQLID`() {
        val id = WithAnnotations::class.declaredMemberProperties.find { it.name == "id" }
        val notId = NoAnnotations::class.declaredMemberProperties.find { it.name == "id" }
        assertTrue { id?.isGraphQLID().isTrue() }
        assertFalse { notId?.isGraphQLID().isTrue() }
    }
}
