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
        @Deprecated("do not use", ReplaceWith("THREE"))
        ONE,
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
        val noDescription = AnnotatedEnum::class.java.getField("TWO")
        assertEquals(expected = "field description", actual = description.graphQLDescription())
        assertNull(noDescription.graphQLDescription())
    }

    @Test
    fun `verify @Deprecated`() {
        val classDeprecation = WithAnnotations::class.getDeprecationReason()
        val propertyDeprecation = AnnotatedEnum::class.java.getField("ONE")?.getDeprecationReason()

        assertEquals(expected = "class deprecated", actual = classDeprecation)
        assertEquals(expected = "do not use, replace with THREE", actual = propertyDeprecation)
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
