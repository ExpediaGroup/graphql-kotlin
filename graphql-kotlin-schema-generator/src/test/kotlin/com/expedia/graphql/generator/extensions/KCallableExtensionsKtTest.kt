package com.expedia.graphql.generator.extensions

import org.junit.jupiter.api.Test
import kotlin.reflect.full.functions
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class KCallableExtensionsKtTest {

    @Suppress("Detekt.FunctionOnlyReturningConstant", "Detekt.UnusedPrivateMember")
    internal open class MyTestClass {
        fun public() = 1

        protected fun protected() = 2

        internal fun internal() = 3

        private fun private() = 4
    }

    @Test
    fun isPublic() {
        assertTrue(MyTestClass::public.isPublic())
        assertFalse(MyTestClass::internal.isPublic())
        assertFalse(MyTestClass::class.functions.find { it.name == "protected" }?.isPublic().isTrue())
        assertFalse(MyTestClass::class.functions.find { it.name == "private" }?.isPublic().isTrue())
    }
}
