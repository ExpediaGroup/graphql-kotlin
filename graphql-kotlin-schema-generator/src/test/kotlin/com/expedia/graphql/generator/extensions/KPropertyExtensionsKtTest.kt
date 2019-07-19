package com.expedia.graphql.generator.extensions

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.annotations.GraphQLName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class KPropertyExtensionsKtTest {

    /**
     * Annotations can be on the property or on the contructor argument
     */
    internal data class MyDataClass(
        @property:Deprecated("property deprecated")
        @property:GraphQLDescription("property description")
        @property:GraphQLID
        @property:GraphQLIgnore
        @property:GraphQLName("nameOnProperty")
        val propertyAnnotation: String,

        @Deprecated("constructor deprecated")
        @GraphQLDescription("constructor description")
        @GraphQLID
        @GraphQLIgnore
        @GraphQLName("nameOnConstructor")
        val constructorAnnotation: String,

        val noAnnotations: String
    )

    @Test
    fun isPropertyGraphQLID() {
        assertTrue(MyDataClass::propertyAnnotation.isPropertyGraphQLID(MyDataClass::class))
        assertTrue(MyDataClass::constructorAnnotation.isPropertyGraphQLID(MyDataClass::class))
        assertFalse(MyDataClass::noAnnotations.isPropertyGraphQLID(MyDataClass::class))
    }

    @Test
    fun isPropertyGraphQLIgnored() {
        assertTrue(MyDataClass::propertyAnnotation.isPropertyGraphQLIgnored(MyDataClass::class))
        assertTrue(MyDataClass::constructorAnnotation.isPropertyGraphQLIgnored(MyDataClass::class))
        assertFalse(MyDataClass::noAnnotations.isPropertyGraphQLIgnored(MyDataClass::class))
    }

    @Test
    fun getPropertyDeprecationReason() {
        assertEquals("property deprecated", MyDataClass::propertyAnnotation.getPropertyDeprecationReason(MyDataClass::class))
        assertEquals("constructor deprecated", MyDataClass::constructorAnnotation.getPropertyDeprecationReason(MyDataClass::class))
        assertEquals(null, MyDataClass::noAnnotations.getPropertyDeprecationReason(MyDataClass::class))
    }

    @Test
    fun getPropertyDescription() {
        assertEquals("property description", MyDataClass::propertyAnnotation.getPropertyDescription(MyDataClass::class))
        assertEquals("constructor description", MyDataClass::constructorAnnotation.getPropertyDescription(MyDataClass::class))
        assertEquals(null, MyDataClass::noAnnotations.getPropertyDescription(MyDataClass::class))
    }

    @Test
    fun getPropertyName() {
        assertEquals("nameOnProperty", MyDataClass::propertyAnnotation.getPropertyName(MyDataClass::class))
        assertEquals("nameOnConstructor", MyDataClass::constructorAnnotation.getPropertyName(MyDataClass::class))
        assertEquals("noAnnotations", MyDataClass::noAnnotations.getPropertyName(MyDataClass::class))
    }
}
