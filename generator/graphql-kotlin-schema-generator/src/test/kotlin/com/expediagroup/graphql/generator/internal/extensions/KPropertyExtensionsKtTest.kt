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

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KPropertyExtensionsKtTest {

    /**
     * Annotations can be on the property or on the contructor argument
     */
    data class MyDataClass(
        @property:Deprecated("property deprecated")
        @property:GraphQLDescription("property description")
        @property:GraphQLIgnore
        @property:GraphQLName("nameOnProperty")
        val propertyAnnotation: String,

        @Deprecated("constructor deprecated")
        @GraphQLDescription("constructor description")
        @GraphQLIgnore
        @GraphQLName("nameOnConstructor")
        val constructorAnnotation: String,

        @get:GraphQLIgnore
        val getterAnnotation: String,

        val noAnnotations: String
    )

    @Test
    fun isPropertyGraphQLIgnored() {
        @Suppress("DEPRECATION")
        assertTrue(MyDataClass::propertyAnnotation.isPropertyGraphQLIgnored(MyDataClass::class))
        @Suppress("DEPRECATION")
        assertTrue(MyDataClass::constructorAnnotation.isPropertyGraphQLIgnored(MyDataClass::class))
        assertTrue(MyDataClass::getterAnnotation.isPropertyGraphQLIgnored(MyDataClass::class))
        assertFalse(MyDataClass::noAnnotations.isPropertyGraphQLIgnored(MyDataClass::class))
    }

    @Test
    fun getPropertyDeprecationReason() {
        @Suppress("DEPRECATION")
        assertEquals("property deprecated", MyDataClass::propertyAnnotation.getPropertyDeprecationReason(MyDataClass::class))
        @Suppress("DEPRECATION")
        assertEquals("constructor deprecated", MyDataClass::constructorAnnotation.getPropertyDeprecationReason(MyDataClass::class))
        assertEquals(null, MyDataClass::noAnnotations.getPropertyDeprecationReason(MyDataClass::class))
    }

    @Test
    fun getPropertyDescription() {
        @Suppress("DEPRECATION")
        assertEquals("property description", MyDataClass::propertyAnnotation.getPropertyDescription(MyDataClass::class))
        @Suppress("DEPRECATION")
        assertEquals("constructor description", MyDataClass::constructorAnnotation.getPropertyDescription(MyDataClass::class))
        assertEquals(null, MyDataClass::noAnnotations.getPropertyDescription(MyDataClass::class))
    }

    @Test
    fun getPropertyName() {
        @Suppress("DEPRECATION")
        assertEquals("nameOnProperty", MyDataClass::propertyAnnotation.getPropertyName(MyDataClass::class))
        @Suppress("DEPRECATION")
        assertEquals("nameOnConstructor", MyDataClass::constructorAnnotation.getPropertyName(MyDataClass::class))
        assertEquals("noAnnotations", MyDataClass::noAnnotations.getPropertyName(MyDataClass::class))
    }
}
