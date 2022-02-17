/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.filters

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.exceptions.InvalidPropertyReturnTypeException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PropertyFiltersTest {

    @Test
    fun `test property filters`() {
        assertTrue(isValidProperty(MyClass::publicProperty, MyClass::class))
        assertFalse(isValidProperty(MyClass::nonPublicProperty, MyClass::class))
        assertFalse(isValidProperty(MyClass::ignoredProperty, MyClass::class))
        assertFalse(isValidProperty(MyClass::kClass, MyDataClass::class))
        assertTrue(isValidProperty(MyDataClass::id, MyDataClass::class))
        assertFalse(isValidProperty(MyDataClass::ignoredProperty, MyDataClass::class))
        assertFalse(isValidProperty(MyClass::foo, MyClass::class))
        assertFalse(isValidProperty(MyClass::`$invalidPropertyName`, MyClass::class))

        assertThrows<InvalidPropertyReturnTypeException> {
            isValidProperty(MyClass::lambda, MyClass::class)
        }
        assertThrows<InvalidPropertyReturnTypeException> {
            isValidProperty(MyClass::suspendableLambda, MyClass::class)
        }
    }

    internal data class MyDataClass(
        val id: Int = 0,

        @GraphQLIgnore
        internal val ignoredProperty: Int = 0
    )

    interface MyInterface {
        @GraphQLIgnore
        val foo: Int
    }

    internal class MyClass(
        val lambda: () -> String,
        val suspendableLambda: suspend () -> String
    ) : MyInterface {

        val publicProperty: Int = 0

        internal val nonPublicProperty: Int = 0

        val kClass: KClass<*> = MyDataClass::class

        @GraphQLIgnore
        internal val ignoredProperty: Int = 0
        override val foo: Int = 5

        val `$invalidPropertyName`: Int = 0
    }

    private fun isValidProperty(property: KProperty<*>, parentClass: KClass<*>): Boolean = propertyFilters.all { it(property, parentClass) }
}
