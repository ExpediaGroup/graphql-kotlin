package com.expediagroup.graphql.generator.filters

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

    @Test
    fun superclassFilters() {
        assertTrue(isValidSuperclass(Interface::class))
        assertFalse(isValidSuperclass(Union::class))
        assertFalse(isValidSuperclass(NonPublic::class))
        assertFalse(isValidSuperclass(Class::class))
    }

    private fun isValidSuperclass(kClass: KClass<*>): Boolean = superclassFilters.all { it(kClass) }
}
