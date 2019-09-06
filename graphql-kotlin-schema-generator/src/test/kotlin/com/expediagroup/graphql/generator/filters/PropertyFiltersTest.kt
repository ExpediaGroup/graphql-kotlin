package com.expediagroup.graphql.generator.filters

import com.expediagroup.graphql.annotations.GraphQLIgnore
import org.junit.jupiter.api.Test
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
    }

    internal data class MyDataClass(
        val id: Int = 0,

        @GraphQLIgnore
        internal val ignoredProperty: Int = 0
    )

    internal class MyClass {

        val publicProperty: Int = 0

        internal val nonPublicProperty: Int = 0

        val kClass: KClass<*> = MyDataClass::class

        @GraphQLIgnore
        internal val ignoredProperty: Int = 0
    }

    private fun isValidProperty(property: KProperty<*>, parentClass: KClass<*>): Boolean = propertyFilters.all { it(property, parentClass) }
}
