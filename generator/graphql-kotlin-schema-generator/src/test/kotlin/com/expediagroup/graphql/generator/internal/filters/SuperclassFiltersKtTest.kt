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

package com.expediagroup.graphql.generator.internal.filters

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SuperclassFiltersKtTest {

    class Class

    interface Interface {
        fun public(): String
    }

    interface Union

    internal interface NonPublic {
        fun internal(): String
    }

    @GraphQLIgnore
    interface IgnoredInterface {
        fun public(): String
    }

    @Test
    fun superclassFilters() {
        assertTrue(isValidSuperclass(Interface::class))
        assertFalse(isValidSuperclass(Union::class))
        assertFalse(isValidSuperclass(NonPublic::class))
        assertFalse(isValidSuperclass(Class::class))
        assertFalse(isValidSuperclass(IgnoredInterface::class))
    }

    private fun isValidSuperclass(kClass: KClass<*>): Boolean = superclassFilters.all { it(kClass) }
}
