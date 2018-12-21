package com.expedia.graphql.generator.extensions

import com.expedia.graphql.annotations.GraphQLDescription
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class FieldExtenstionsKtTest {

    internal enum class AnnotatedEnum {
        @GraphQLDescription("field description")
        @Deprecated("do not use", ReplaceWith("TWO"))
        ONE,
        TWO
    }

    @Test
    fun `verify @GraphQLDescrption on fields`() {
        val description = AnnotatedEnum::class.java.getField("ONE")
        val noDescription = AnnotatedEnum::class.java.getField("TWO")
        assertEquals(expected = "field description", actual = description.getGraphQLDescription())
        assertNull(noDescription.getGraphQLDescription())
    }

    @Test
    fun `verify @Deprecated on fields`() {
        val propertyDeprecation = AnnotatedEnum::class.java.getField("ONE")?.getDeprecationReason()
        assertEquals(expected = "do not use, replace with TWO", actual = propertyDeprecation)
    }
}
