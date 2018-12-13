package com.expedia.graphql.schema.extensions

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.annotations.GraphQLIgnore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class KPropertyExtensionsKtTest {

    /**
     * Annotations can be on the property or on the contructor argument
     */
    internal class MyClass(
        @property:Deprecated("property deprecated")
        @property:GraphQLDescription("property description")
        @property:GraphQLID
        @property:GraphQLIgnore
        val propertyAnnotation: String,

        @property:Deprecated("property Int deprecated")
        @property:GraphQLDescription("property Int description")
        @property:GraphQLID
        @property:GraphQLIgnore
        val propertyAnnotationInt: Int,

        @Deprecated("constructor deprecated")
        @GraphQLDescription("constructor description")
        @GraphQLID
        @GraphQLIgnore
        val constructorAnnotation: String,

        val noAnnotations: String
    )

    @Test
    fun isPropertyGraphQLID() {
        assertTrue(MyClass::propertyAnnotation.isPropertyGraphQLID(MyClass::class))
        assertTrue(MyClass::propertyAnnotationInt.isPropertyGraphQLID(MyClass::class))
        assertTrue(MyClass::constructorAnnotation.isPropertyGraphQLID(MyClass::class))
        assertFalse(MyClass::noAnnotations.isPropertyGraphQLID(MyClass::class))
    }

    @Test
    fun isPropertyGraphQLIgnored() {
        assertTrue(MyClass::propertyAnnotation.isPropertyGraphQLIgnored(MyClass::class))
        assertTrue(MyClass::propertyAnnotationInt.isPropertyGraphQLIgnored(MyClass::class))
        assertTrue(MyClass::constructorAnnotation.isPropertyGraphQLIgnored(MyClass::class))
        assertFalse(MyClass::noAnnotations.isPropertyGraphQLIgnored(MyClass::class))
    }

    @Test
    fun getPropertyDeprecationReason() {
        assertEquals("property deprecated", MyClass::propertyAnnotation.getPropertyDeprecationReason(MyClass::class))
        assertEquals("property Int deprecated", MyClass::propertyAnnotationInt.getPropertyDeprecationReason(MyClass::class))
        assertEquals("constructor deprecated", MyClass::constructorAnnotation.getPropertyDeprecationReason(MyClass::class))
        assertEquals(null, MyClass::noAnnotations.getPropertyDeprecationReason(MyClass::class))
    }

    @Test
    fun getPropertyDescription() {
        assertEquals("property description", MyClass::propertyAnnotation.getPropertyDescription(MyClass::class))
        assertEquals("property Int description", MyClass::propertyAnnotationInt.getPropertyDescription(MyClass::class))
        assertEquals("constructor description", MyClass::constructorAnnotation.getPropertyDescription(MyClass::class))
        assertEquals(null, MyClass::noAnnotations.getPropertyDescription(MyClass::class))
    }
}
