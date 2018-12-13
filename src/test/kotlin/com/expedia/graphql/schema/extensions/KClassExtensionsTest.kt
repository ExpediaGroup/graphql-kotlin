package com.expedia.graphql.schema.extensions

import com.expedia.graphql.schema.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class KClassExtensionsTest {

    @Suppress("Detekt.FunctionOnlyReturningConstant", "Detekt.UnusedPrivateMember")
    private class MyTestClass(
        val publicProperty: String = "public",
        val filteredProperty: String = "filtered",
        private val privateVal: String = "hidden"
    ) : TestInterface {
        fun publicFunction() = "public function"

        fun filteredFunction() = "filtered function"

        private fun privateTestFunction() = "private function"
    }

    private enum class MyTestEnum {
        ONE,
        TWO
    }

    private class EmptyConstructorClass {
        val id = 1
    }

    private interface TestInterface

    private interface InvalidPropertyUnionInterface {
        val test: Int
            get() = 1
    }

    @Suppress("Detekt.FunctionOnlyReturningConstant")
    private interface InvalidFunctionUnionInterface {
        fun getTest() = 1
    }

    private class FilterHooks : SchemaGeneratorHooks {
        override fun isValidProperty(property: KProperty<*>) =
            property.name.contains("filteredProperty").not()

        override fun isValidFunction(function: KFunction<*>) =
            function.name.contains("filteredFunction").not()
    }

    private val noopHooks = NoopSchemaGeneratorHooks()

    @Test
    fun `test getting valid properties with no hooks`() {
        val properties = MyTestClass::class.getValidProperties(noopHooks)
        assertEquals(listOf("filteredProperty", "publicProperty"), properties.map { it.name })
    }

    @Test
    fun `test getting valid properties with filter hooks`() {
        val properties = MyTestClass::class.getValidProperties(FilterHooks())
        assertEquals(listOf("publicProperty"), properties.map { it.name })
    }

    @Test
    fun `test getting valid functions with no hooks`() {
        val properties = MyTestClass::class.getValidFunctions(noopHooks)
        assertEquals(listOf("filteredFunction", "publicFunction"), properties.map { it.name })
    }

    @Test
    fun `test getting valid functions with filter hooks`() {
        val properties = MyTestClass::class.getValidFunctions(FilterHooks())
        assertEquals(listOf("publicFunction"), properties.map { it.name })
    }

    @Test
    fun `test findConstructorParamter`() {
        assertNotNull(MyTestClass::class.findConstructorParamter("publicProperty"))
        assertNull(MyTestClass::class.findConstructorParamter("foobar"))
        assertNull(EmptyConstructorClass::class.findConstructorParamter("id"))
        assertNull(TestInterface::class.findConstructorParamter("foobar"))
    }

    @Test
    fun `test enum extension`() {
        assertTrue(MyTestEnum::class.isEnum())
        assertFalse(MyTestClass::class.isEnum())
    }

    @Test
    fun `test list extension`() {
        assertTrue(listOf(1)::class.isList())
        assertTrue(arrayListOf(1)::class.isList())
        assertFalse(arrayOf(1)::class.isList())
        assertFalse(MyTestClass::class.isList())
    }

    @Test
    fun `test array extension`() {
        assertTrue(arrayOf(1)::class.isArray())
        assertTrue(intArrayOf(1)::class.isArray())
        assertFalse(listOf(1)::class.isArray())
        assertFalse(MyTestClass::class.isArray())
    }

    @Test
    fun `test graphql interface extension`() {
        assertTrue(TestInterface::class.isGraphQLInterface())
        assertFalse(MyTestClass::class.isGraphQLInterface())
    }

    @Test
    fun `test graphql union extension`() {
        assertTrue(TestInterface::class.isGraphQLUnion())
        assertFalse(InvalidPropertyUnionInterface::class.isGraphQLUnion())
        assertFalse(InvalidFunctionUnionInterface::class.isGraphQLUnion())
    }
}
