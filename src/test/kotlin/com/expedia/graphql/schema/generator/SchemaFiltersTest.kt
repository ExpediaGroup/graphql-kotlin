package com.expedia.graphql.schema.generator

import com.expedia.graphql.annotations.GraphQLIgnore
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class SchemaFiltersTest {

    private data class MyDataClass(val id: Int = 0)

    private class MyClass {

        val publicProperty: Int = 0

        internal val nonPublicProperty: Int = 0

        @GraphQLIgnore
        internal val ignoredProperty: Int = 0

        fun publicFunction() = privateFunction()

        @GraphQLIgnore
        fun ignoredFunction() = privateFunction()

        internal fun privateFunction() = nonPublicProperty
    }

    @Test
    fun `test function filters`() {
        assertTrue(testFunction(MyClass::publicFunction))
        assertFalse(testFunction(MyClass::ignoredFunction))
        assertFalse(testFunction(MyClass::privateFunction))
    }

    @Test
    fun `test generated function filters`() {
        val functions = MyDataClass::class.declaredMemberFunctions
        val size = functions.filter { func -> functionFilters.all { it(func) } }.size
        assertEquals(expected = 0, actual = size)
    }

    @Test
    fun `test property filters`() {
        assertTrue(testProperty(MyClass::publicProperty))
        assertFalse(testProperty(MyClass::nonPublicProperty))
        assertFalse(testProperty(MyClass::ignoredProperty))
        assertTrue(testProperty(MyDataClass::id))
    }

    private fun testFunction(function: KFunction<*>): Boolean = functionFilters.all { it(function) }

    private fun testProperty(property: KProperty<*>): Boolean = propertyFilters.all { it(property) }
}
