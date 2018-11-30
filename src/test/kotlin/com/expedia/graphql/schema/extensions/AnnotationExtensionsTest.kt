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

internal class AnnotationExtensionsTest {

    @GraphQLDescription("class description")
    @Deprecated("class deprecated")
    @GraphQLIgnore
    private data class WithAnnotations(
        @Deprecated("property deprecated")
        @property:GraphQLDescription("property description")
        @property:GraphQLID
        val id: String
    )

    private data class NoAnnotations(val id: String)

    private enum class AnnotatedEnum {
        @GraphQLDescription("field description")
        ONE
    }

    private enum class NoAnnoationsEnum {
        TWO
    }

    @Test
    fun `verify @GraphQLDescrption on classes`() {
        assertEquals(expected = "class description", actual = WithAnnotations::class.graphQLDescription())
        assertNull(NoAnnotations::class.graphQLDescription())
    }

    @Test
    fun `verify @GraphQLDescrption on fields`() {
        val description = AnnotatedEnum::class.java.getField("ONE")
        val noDescription = NoAnnoationsEnum::class.java.getField("TWO")
        assertEquals(expected = "field description", actual = description.graphQLDescription())
        assertNull(noDescription.graphQLDescription())
    }

    @Test
    fun `verify @Deprecated`() {
        assertEquals(expected = "class deprecated", actual = WithAnnotations::class.getDeprecationReason())
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
        assertTrue { id?.isGraphQLID() == true }
        assertTrue { notId?.isGraphQLID() == false }
    }
}
