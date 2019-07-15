package com.expedia.graphql.generator.filters

import com.expedia.graphql.annotations.GraphQLIgnore
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class CallableFiltersTest {

    @Test
    fun `test function filters`() {
        assertTrue(isValidFunction(MyClass::publicFunction))
        assertFalse(isValidFunction(MyClass::ignoredFunction))
        assertFalse(isValidFunction(MyClass::privateFunction))
    }

    @Test
    fun `test generated function filters`() {
        val functions = MyDataClass::class.declaredMemberFunctions
        val size = functions.filter { func -> functionFilters.all { it(func) } }.size
        assertEquals(expected = 0, actual = size)
    }

    internal data class MyDataClass(val id: Int = 0)

    internal class MyClass {
        fun publicFunction() = privateFunction()

        @GraphQLIgnore
        fun ignoredFunction() = privateFunction()

        @Suppress("Detekt.FunctionOnlyReturningConstant")
        internal fun privateFunction() = 0
    }

    private fun isValidFunction(function: KFunction<*>): Boolean = functionFilters.all { it(function) }
}
